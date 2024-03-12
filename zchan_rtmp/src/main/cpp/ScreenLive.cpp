#include <iostream>
#include <jni.h>


#include <queue>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>
#include <thread>

extern "C" {
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswresample/swresample.h>
#include <libavutil/time.h>
}

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"PUSH",FORMAT,##__VA_ARGS__);

using namespace cv;
using namespace std;

AVFormatContext *ic = NULL;
bool isRunning = true;
bool isExit[3] = {0};

mutex packs_mux;
queue<AVPacket *> packs;


SwsContext *vsc = NULL;
AVFrame *yuv_frame = NULL;
AVCodecContext *vc = NULL;
AVStream *vs = NULL;


SwrContext *asc = nullptr;
AVFrame *pcm_frame = NULL;
AVCodecContext *ac = NULL;
AVStream *as = NULL;


struct NV12Data {
    int8_t *ydata;
    int8_t *uvdata;
    int yline_size;
    int uvline_size;
    int64_t pts;
};

struct PCMData {
    uint8_t *data;
    int size;
    int64_t pts;
};

queue<NV12Data> video_queue;
mutex video_mux;

queue<PCMData> audio_queue;
mutex audio_mux;


void video() {
    AVPacket *pkt = av_packet_alloc();
    while (isRunning) {
        if (packs.size() > 100) {
            av_usleep(1);
            continue;
        }

        if (video_queue.empty()) {
            av_usleep(1);
            continue;
        }

        video_mux.lock();
        NV12Data video_data = video_queue.front();
        video_queue.pop();
        video_mux.unlock();

        int8_t *y_data = video_data.ydata;
        int8_t *uv_data = video_data.uvdata;

        yuv_frame->data[0] = (uint8_t *) y_data;
        yuv_frame->data[1] = (uint8_t *) uv_data;

        yuv_frame->linesize[0] = video_data.yline_size;
        yuv_frame->linesize[1] = video_data.uvline_size;
        yuv_frame->pts = video_data.pts;
        int re = avcodec_send_frame(vc, yuv_frame);
        if (re != 0) {
            LOGE("avcodec_send_frame failed");
            continue;
        }
        avcodec_receive_packet(vc, pkt);

        //free video_data's memory
        free(y_data);
        free(uv_data);
        pkt->pts = av_rescale_q(pkt->pts, vc->time_base, vs->time_base);
        pkt->dts = av_rescale_q(pkt->dts, vc->time_base, vs->time_base);
        pkt->duration = av_rescale_q(pkt->duration, vc->time_base, vs->time_base);
        pkt->stream_index = vs->index;
        AVPacket *video_pack = av_packet_clone(pkt);

        packs_mux.lock();
        packs.push(video_pack);
        packs_mux.unlock();

    }
    isExit[0] = true;
}

void audio() {

    AVPacket *pkt = av_packet_alloc();
    while (isRunning) {

        if (packs.size() > 100) {
            av_usleep(1);
            continue;
        }

        if (audio_queue.empty()) {
            av_usleep(1);
            continue;
        }


        audio_mux.lock();
        PCMData audio_data = audio_queue.front();
        audio_queue.pop();
        audio_mux.unlock();

        const uint8_t *indata[AV_NUM_DATA_POINTERS] = {0};
        indata[0] = audio_data.data;
        swr_convert(asc,
                    pcm_frame->data, pcm_frame->nb_samples,
                    indata, pcm_frame->nb_samples);
        pcm_frame->pts = audio_data.pts;
        free(audio_data.data);//clean audio_data's memory
        avcodec_send_frame(ac, pcm_frame);
        avcodec_receive_packet(ac, pkt);

        pkt->pts = av_rescale_q(pkt->pts, ac->time_base, as->time_base);
        pkt->dts = av_rescale_q(pkt->dts, ac->time_base, as->time_base);
        pkt->duration = av_rescale_q(pkt->duration, ac->time_base, as->time_base);
        pkt->stream_index = as->index;

        AVPacket *audio_pack = av_packet_clone(pkt);
        packs_mux.lock();
        packs.push(audio_pack);
        packs_mux.unlock();
    }
    isExit[1] = true;


}

void push_thread() {
    while (isRunning) {
        packs_mux.lock();
        if (packs.empty()) {
            packs_mux.unlock();
            av_usleep(1);
            continue;
        }
        AVPacket *pkt = packs.front();
        packs.pop();
        //out put the type of packet with the time pts
        LOGE("index : %d, pts: %lld", pkt->stream_index, pkt->pts);
        packs_mux.unlock();
        av_interleaved_write_frame(ic, pkt);
        av_packet_free(&pkt);
    }
    isExit[2] = true;
}

void init(int width, int height, const char *url) {
    //set isExit all false
    LOGE("width: %d, height: %d", width, height);
    isRunning = true;
    for (int i = 0; i < 3; ++i) {
        isExit[i] = false;
    }

    avcodec_register_all();
    av_register_all();
    avformat_network_init();


    avformat_alloc_output_context2(&ic, 0, "flv", url);

    yuv_frame = av_frame_alloc();
    yuv_frame->format = AV_PIX_FMT_NV12;
    yuv_frame->width = width;
    yuv_frame->height = height;
    yuv_frame->pts = 0;
    av_frame_get_buffer(yuv_frame, 32);

    AVCodec *video_codec = avcodec_find_encoder(AV_CODEC_ID_H264);
    vc = avcodec_alloc_context3(video_codec);
    vc->flags |= AV_CODEC_FLAG_GLOBAL_HEADER; //全局参数
    vc->codec_id = video_codec->id;
    vc->thread_count = 8;
    vc->bit_rate = 20 * 1024 * 8;
    vc->width = width;
    vc->height = height;
    vc->time_base = {1, 1000000};
    vc->framerate = {33, 1};
    vc->gop_size = 25;
    vc->max_b_frames = 1;
    vc->pix_fmt = AV_PIX_FMT_NV12;
    avcodec_open2(vc, video_codec, 0);


    asc = swr_alloc_set_opts(asc,
                             av_get_default_channel_layout(2),
                             AV_SAMPLE_FMT_FLTP,
                             44100,
                             av_get_default_channel_layout(2),
                             AV_SAMPLE_FMT_S16,
                             44100,
                             0,
                             nullptr);
    swr_init(asc);
    pcm_frame = av_frame_alloc();
    pcm_frame->format = AV_SAMPLE_FMT_FLTP;
    pcm_frame->channels = 2;
    pcm_frame->channel_layout = av_get_default_channel_layout(2);
    pcm_frame->nb_samples = 1024;//一帧音频单通道的采样数量
    pcm_frame->pts = 0;
    av_frame_get_buffer(pcm_frame, 0);
    AVCodec *audio_codec = avcodec_find_encoder(AV_CODEC_ID_AAC);
    ac = avcodec_alloc_context3(audio_codec);
    ac->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
    ac->codec_id = audio_codec->id;
    ac->sample_fmt = AV_SAMPLE_FMT_FLTP;
    ac->sample_rate = 44100;
    ac->channel_layout = av_get_default_channel_layout(2);
    ac->channels = 2;
    ac->thread_count = 8;
    ac->bit_rate = 40000;
    ac->time_base = {1, 1000000};
    avcodec_open2(ac, audio_codec, nullptr);

    as = avformat_new_stream(ic, NULL);
    as->codecpar->codec_tag = 0;
    avcodec_parameters_from_context(as->codecpar, ac);

    vs = avformat_new_stream(ic, NULL);
    vs->codecpar->codec_tag = 0;
    avcodec_parameters_from_context(vs->codecpar, vc);

    int re = avio_open(&ic->pb, url, AVIO_FLAG_WRITE);
    if (re != 0) {
        LOGE("avio_open failed");
        return;
    }
    LOGE("avio_open success")
    avformat_write_header(ic, NULL);
}




void startPush() {
    isRunning = true;
    thread vt(video);
    thread at(audio);
    thread p(push_thread);
    vt.detach();
    at.detach();
    p.detach();
}

void stopPush() {
    isRunning = false;
    while (!isExit[0] || !isExit[1] || !isExit[2]) {
        av_usleep(1);
    }
    LOGE("stopPush")
    if (ic) {
        av_write_trailer(ic);
        avio_close(ic->pb);
        avformat_free_context(ic);
        ic = NULL;
    }
    LOGE("ic free")
    if (vc) {
        avcodec_close(vc);
        avcodec_free_context(&vc);
        vc = NULL;
    }
    LOGE("vc free")
    if (ac) {
        avcodec_close(ac);
        avcodec_free_context(&ac);
        ac = NULL;
    }
    LOGE("ac free")
    if (yuv_frame) {
        av_frame_free(&yuv_frame);
        yuv_frame = NULL;
    }
    LOGE("yuv_frame free")
    if (pcm_frame) {
        av_frame_free(&pcm_frame);
        pcm_frame = NULL;
    }
    LOGE("pcm_frame free")

    if (vsc) {
        sws_freeContext(vsc);
        vsc = NULL;
    }
    LOGE("vsc free")
    if (asc) {
        swr_free(&asc);
        asc = NULL;
    }
    LOGE("asc free")
    while (!packs.empty()) {
        AVPacket *pkt = packs.front();
        av_packet_free(&pkt);
        packs.pop();
    }
    LOGE("packs free")
    while (!audio_queue.empty()) {
        PCMData audio_data = audio_queue.front();
        free(audio_data.data);
        audio_queue.pop();
    }
    LOGE("audio_queue free")
    while (!video_queue.empty()) {
        NV12Data data = video_queue.front();
        free(data.ydata);
        free(data.uvdata);
        video_queue.pop();
    }
    LOGE("video_queue free")

    LOGE("stopPush");


}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_pushVideo(JNIEnv *env, jclass clazz, jlong mat_addr) {

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_pushAudio(JNIEnv *env, jclass clazz, jbyteArray data,
                                               jint len) {
    jbyte *audio_data = env->GetByteArrayElements(data, NULL);
    PCMData pcmData;
    uint8_t *audio = (uint8_t *) malloc(len);
    memcpy(audio, audio_data, len);
    pcmData.data = audio;
    pcmData.size = len;
    pcmData.pts = av_gettime();
    LOGE("audio pts: %lld", pcmData.pts);
    while (audio_queue.size() > 100 && isRunning) {
        av_usleep(1);
    }
    audio_mux.lock();
    audio_queue.push(pcmData);
    LOGE("audio_queue size: %d", audio_queue.size());
    audio_mux.unlock();
    env->ReleaseByteArrayElements(data, audio_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_init(JNIEnv *env, jclass clazz, jint width, jint height,
                                          jstring url) {
    const char *outUrl = env->GetStringUTFChars(url, 0);
    init(width, height, outUrl);
    env->ReleaseStringUTFChars(url, outUrl);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_stopPush(JNIEnv *env, jclass clazz) {
    stopPush();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_startPush(JNIEnv *env, jclass clazz) {
    startPush();
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1rtmp_LiveImp_pushVideoYUV420NV12(JNIEnv *env, jclass clazz, jbyteArray y,
                                                         jbyteArray uv, jint width, jint height) {
    jbyte *y_data = env->GetByteArrayElements(y, NULL);
    jbyte *uv_data = env->GetByteArrayElements(uv, NULL);
    int y_len = env->GetArrayLength(y);
    int uv_len = env->GetArrayLength(uv);

    NV12Data data;
    int8_t *ydata = (int8_t *) malloc(y_len);
    int8_t *uvdata = (int8_t *) malloc(uv_len);
    memcpy(ydata, y_data, y_len);
    memcpy(uvdata, uv_data, uv_len);
    data.ydata = ydata;
    data.uvdata = uvdata;
    data.yline_size = width;
    data.uvline_size = width;
    data.pts = av_gettime();
    while (video_queue.size() > 100 && isRunning) {
        av_usleep(1);
    }
    video_mux.lock();
    video_queue.push(data);
    video_mux.unlock();
    env->ReleaseByteArrayElements(y, y_data, 0);
    env->ReleaseByteArrayElements(uv, uv_data, 0);
}