#include <iostream>
#include <jni.h>


#include <queue>
#include <thread>
#include "Packet.cxx"
#include "AudioEncoder.cxx"

extern "C" {
#include <libavutil/avutil.h>
#include <libswscale/swscale.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavutil/time.h>
}

#include "PushQueue.cxx"
#include "VideoEncoder.cxx"
#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"PUSH",FORMAT,##__VA_ARGS__);

#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1ffrtmp_1plus_JniImp_##name

AVFormatContext *ic = NULL;
AVStream *vs = NULL;
AVStream *as = NULL;
VideoEncoder *videoEncoder = NULL;
AudioEncoder *audioEncoder = NULL;
bool isRunning = true;
bool isExit[2] = {false};
static PushQueue *pushQueue = new PushQueue();


void audioThread() {
    LOGE("audioThread")
    while (isRunning) {
        LOGE("audioThread loop")
        if (audioEncoder == NULL) {
            av_usleep(100);
            LOGE("audioEncoder is null")
            continue;
        }
        Packet audio_pkt = audioEncoder->receive();
        if (audio_pkt.data[0] == NULL) { //return Packet()
            av_usleep(100);
            continue;
        }
        AVPacket *apkt = (AVPacket *) audio_pkt.data[0];
        apkt->pts = av_rescale_q(apkt->pts, audioEncoder->ac->time_base, as->time_base);
        apkt->dts = av_rescale_q(apkt->dts, audioEncoder->ac->time_base, as->time_base);
        apkt->duration = av_rescale_q(apkt->duration, audioEncoder->ac->time_base, as->time_base);
        apkt->stream_index = as->index;
        pushQueue->pushPacket(apkt);
    }
    isExit[0] = true;

}

void videoThread() {
    LOGE("videoThread")
    while (isRunning) {
        LOGE("videoThread loop")
        if (videoEncoder == NULL) {
            av_usleep(100);
            LOGE("videoEncoder is null")
            continue;
        }
        Packet video_pkt = videoEncoder->receive();
        if (video_pkt.data[0] == NULL) {
            av_usleep(100);
            continue;
        }
        AVPacket *vpkt = (AVPacket *) video_pkt.data[0];
        vpkt->pts = av_rescale_q(vpkt->pts, videoEncoder->vc->time_base, vs->time_base);
        vpkt->dts = av_rescale_q(vpkt->dts, videoEncoder->vc->time_base, vs->time_base);
        vpkt->duration = av_rescale_q(vpkt->duration, videoEncoder->vc->time_base, vs->time_base);
        vpkt->stream_index = vs->index;
        pushQueue->pushPacket(vpkt);

    }
    isExit[1] = true;

}

void pushThread() {
    LOGE("pushThread")
    while (isRunning) {
        LOGE("pushThread loop")
        AVPacket *pkt = pushQueue->popPacket();
        if (pkt == nullptr || pkt->data == nullptr || pkt->pts < 0) { //dirty data
            av_usleep(100);
            continue;
        }
        av_interleaved_write_frame(ic, pkt);
        av_packet_free(&pkt);
    }
}

JNI_FUNC(pushNV12)(JNIEnv *env, jclass clazz, jbyteArray y,
                   jbyteArray uv) {
    if (videoEncoder == NULL) {
        LOGE("videoEncoder is null")
        return;
    }
    jbyte *y_data = env->GetByteArrayElements(y, NULL);
    jbyte *uv_data = env->GetByteArrayElements(uv, NULL);
    int y_len = env->GetArrayLength(y);
    int uv_len = env->GetArrayLength(uv);
    Packet data;
    int8_t *ydata = (int8_t *) malloc(y_len);
    int8_t *uvdata = (int8_t *) malloc(uv_len);
    memcpy(ydata, y_data, y_len);
    memcpy(uvdata, uv_data, uv_len);
    data.data[0] = ydata;
    data.data[1] = uvdata;
    data.line_size[0] = videoEncoder->vc->width;
    data.line_size[1] = videoEncoder->vc->width;
    data.pts = av_gettime();
    data.type = 0;//NV12
    videoEncoder->send(data);
    env->ReleaseByteArrayElements(y, y_data, 0);
    env->ReleaseByteArrayElements(uv, uv_data, 0);

}
JNI_FUNC(init)(JNIEnv *env, jclass clazz, jstring url, jint width,
               jint height) {
    const char *url_ = env->GetStringUTFChars(url, NULL);

    avcodec_register_all();
    av_register_all();
    avformat_network_init();


    videoEncoder = new VideoEncoder(width, height);
    audioEncoder = new AudioEncoder();


    avformat_alloc_output_context2(&ic, 0, "flv", url_);

    as = avformat_new_stream(ic, NULL);
    as->codecpar->codec_tag = 0;
    avcodec_parameters_from_context(as->codecpar, audioEncoder->ac);

    vs = avformat_new_stream(ic, NULL);
    vs->codecpar->codec_tag = 0;
    avcodec_parameters_from_context(vs->codecpar, videoEncoder->vc);


    avio_open(&ic->pb, url_, AVIO_FLAG_WRITE);
    avformat_write_header(ic, NULL);

    env->ReleaseStringUTFChars(url, url_);
}
JNI_FUNC(startFRtmp)(JNIEnv *env, jclass clazz) {
    LOGE("startFRtmp")
    std::thread a(audioThread);
    std::thread v(videoThread);
    std::thread p(pushThread);
    a.detach();
    v.detach();
    p.detach();

}

JNI_FUNC(pushPCM)(JNIEnv *env, jclass clazz, jbyteArray data) {
    if (audioEncoder == NULL) {
        LOGE("audioEncoder is null")
        return;
    }
    jbyte *audio_data = env->GetByteArrayElements(data, NULL);
    int len = env->GetArrayLength(data);
    Packet pkt;
    int8_t *pcm_data = (int8_t *) malloc(len);
    memcpy(pcm_data, audio_data, len);
    pkt.data[0] = pcm_data;
    pkt.pts = av_gettime();
    audioEncoder->send(pkt);
    env->ReleaseByteArrayElements(data, audio_data, 0);
}
JNI_FUNC(stopFRtmp)(JNIEnv *env, jclass clazz) {
    isRunning = false;
    while (!isExit[0] || !isExit[1]) {
        av_usleep(100);
    }

    while (pushQueue->size() > 0) {
        AVPacket *pkt = pushQueue->popPacket();
        av_packet_free(&pkt);
    }

    av_write_trailer(ic);
    avio_close(ic->pb);
    avformat_free_context(ic);
    isRunning = true;
    isExit[0] = false;
    isExit[1] = false;

    delete videoEncoder;
    delete audioEncoder;
    videoEncoder = NULL;
    audioEncoder = NULL;
}
JNI_FUNC(pushI420)(JNIEnv *env, jclass clazz, jbyteArray y,
                   jbyteArray u, jbyteArray v) {
    if (videoEncoder == NULL) {
        LOGE("videoEncoder is null")
        return;
    }
    jbyte *y_data = env->GetByteArrayElements(y, NULL);
    jbyte *u_data = env->GetByteArrayElements(u, NULL);
    jbyte *v_data = env->GetByteArrayElements(v, NULL);
    int y_len = env->GetArrayLength(y);
    int u_len = env->GetArrayLength(u);
    int v_len = env->GetArrayLength(v);
    Packet data;
    int8_t *ydata = (int8_t *) malloc(y_len);
    int8_t *udata = (int8_t *) malloc(u_len);
    int8_t *vdata = (int8_t *) malloc(v_len);
    memcpy(ydata, y_data, y_len);
    memcpy(udata, u_data, u_len);
    memcpy(vdata, v_data, v_len);
    data.data[0] = ydata;
    data.data[1] = udata;
    data.data[2] = vdata;
    data.line_size[0] = videoEncoder->vc->width;
    data.line_size[1] = videoEncoder->vc->width / 2;
    data.line_size[2] = videoEncoder->vc->width / 2;
    data.pts = av_gettime();
    data.type = 1;//I420
    videoEncoder->send(data);
    env->ReleaseByteArrayElements(y, y_data, 0);
    env->ReleaseByteArrayElements(u, u_data, 0);
    env->ReleaseByteArrayElements(v, v_data, 0);

}