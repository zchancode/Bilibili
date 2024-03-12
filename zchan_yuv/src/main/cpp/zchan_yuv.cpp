#include <jni.h>
#include <string>
#include <android/log.h>
#include <queue>
#include <mutex>
#include <thread>

extern "C" {
#include <libavutil/time.h>
#include <libavformat/avformat.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <libswresample/swresample.h>
#include <libavutil/imgutils.h>
#include <libavutil/opt.h>
#include <libswscale/swscale.h>
#include <libavcodec/jni.h>
}
#define TAG "zchan_yuv"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

using namespace std;

AVFormatContext *avFormatContext;
int64_t totalMs;

//involves structs including AVFrame, AVPacket, AVCodecContext
void openFile(std::string url) {
    avformat_open_input(&avFormatContext, url.c_str(), 0, 0);//open mp4 file
    avformat_find_stream_info(avFormatContext,
                              NULL);//get stream info such as video stream, audio stream info
    totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
}

int videoStreamIndex;
AVCodecParameters *v_para;
AVCodecContext *v_codec;

void openDecoder() {
    videoStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);
    LOGE("audioStream index: %d", videoStreamIndex);
    v_para = avFormatContext->streams[videoStreamIndex]->codecpar;
    LOGE("v_para->codec_id: %d", v_para->codec_id);
    LOGE("v_para->width: %d", v_para->width);
    LOGE("v_para->height: %d", v_para->height);
    LOGE("v_para->format: %d", v_para->format);
    LOGE("v_para->bit_rate: %lld", v_para->bit_rate);
    AVCodec *cd = avcodec_find_decoder(v_para->codec_id);
    v_codec = avcodec_alloc_context3(cd);
    avcodec_parameters_to_context(v_codec, v_para);
    LOGE("v_codec context -> pix_fmt: %d", v_codec->pix_fmt);
    v_codec->thread_count = 8;
    avcodec_open2(v_codec, 0, 0);
}

double r2d(AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
}

bool isRunning = true;
bool isExit[2] = {false};
queue<AVPacket *> videoPackets;
mutex videoPacketsMutex;

void readPacket() {
    AVPacket *packet = av_packet_alloc();
    int ret = av_read_frame(avFormatContext, packet);
    if (ret != 0) {
        LOGE("end of file");
        av_packet_free(&packet);
        isRunning = false;
        return;
    }
    if (packet->stream_index != videoStreamIndex) {
        av_packet_free(&packet);
        return;
    }

    packet->pts =
            packet->pts * (r2d(avFormatContext->streams[packet->stream_index]->time_base) * 1000);
    videoPackets.push(packet);
}

void readPacketLoop() {
    LOGE("readPacketLoop");
    while (isRunning) {
        videoPacketsMutex.lock();
        if (videoPackets.size() < 100) {
            readPacket();
        }
        videoPacketsMutex.unlock();
        av_usleep(10);
    }
    isExit[0] = true;
    LOGE("readPacketLoop end");
}

struct RgbData {
    int width;
    int height;
    uint8_t *data;
public:
    void release() {
        delete[] data;
    }
};

queue<RgbData> RgbDatas;
mutex RgbDatasMutex;

SwsContext *sws_ctx;

void initSws(int width, int height, AVPixelFormat format) {
    sws_ctx = sws_getContext(width, height, format,
                             width, height, (AVPixelFormat) AV_PIX_FMT_RGBA,
                             SWS_BILINEAR, NULL, NULL, NULL);
}

AVFrame *frame = nullptr;

void decodePacket() {
    if (frame == nullptr) {
        frame = av_frame_alloc();
    }
    AVPacket *packet = nullptr;
    packet = videoPackets.front();
    videoPackets.pop();
    avcodec_send_packet(v_codec, packet);
    av_packet_free(&packet);
    avcodec_receive_frame(v_codec, frame);
    LOGE("frame pts: %lld", frame->pts);
    if (frame->pts <= 0) {
        return;
    }
    LOGE("y: %d", frame->linesize[0]);
    LOGE("u: %d", frame->linesize[1]);
    LOGE("v: %d", frame->linesize[2]);
    LOGE("width: %d", frame->width);
    //format
    LOGE("format: %d", frame->format);
    if (sws_ctx == nullptr) {
        initSws(frame->width, frame->height, (AVPixelFormat) frame->format);
    }
    uint8_t *rgb = new uint8_t[frame->width * frame->height * 4];
    int rgb_linesize[1] = {frame->width * 4};
    sws_scale(sws_ctx, frame->data, frame->linesize, 0, frame->height, &rgb,
              rgb_linesize);
    RgbData rgbData;
    rgbData.width = frame->width;
    rgbData.height = frame->height;
    rgbData.data = rgb;
    while (true){
        RgbDatasMutex.lock();
        if (RgbDatas.size() < 100) {
            RgbDatas.push(rgbData);
            RgbDatasMutex.unlock();
            break;
        }
        RgbDatasMutex.unlock();
        av_usleep(10);
    }

}

void decodePacketLoop() {
    LOGE("decodePacketLoop");
    while (isRunning) {
        videoPacketsMutex.lock();
        if (videoPackets.empty()) {
            videoPacketsMutex.unlock();
            av_usleep(10);
            continue;
        }
        decodePacket();
        videoPacketsMutex.unlock();
    }
    isExit[1] = true;
    LOGE("decodePacketLoop end");
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1yuv_ui_YuvActivity_playYuv(JNIEnv *env, jobject thiz, jstring path) {
    const char *input = env->GetStringUTFChars(path, 0);
    LOGE("input path: %s", input);
    env->ReleaseStringUTFChars(path, input);
    av_register_all();
    avcodec_register_all();
    avformat_network_init();
    openFile(input);
    LOGE("totalMs: %lld", totalMs);
    openDecoder();
    thread readPacket(readPacketLoop);
    thread decodePacket(decodePacketLoop);
    readPacket.detach();
    decodePacket.detach();

}



extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_zchan_1yuv_ui_YuvActivity_getRgbFrame(JNIEnv *env, jobject thiz) {
    while (true) {
        RgbDatasMutex.lock();
        if (!RgbDatas.empty()) {
            RgbData rgbData = RgbDatas.front();
            jbyteArray byteArray = env->NewByteArray(rgbData.width * rgbData.height * 4);
            env->SetByteArrayRegion(byteArray, 0, rgbData.width * rgbData.height * 4,
                                    (jbyte *) rgbData.data);
            RgbDatas.pop();
            rgbData.release();

            RgbDatasMutex.unlock();
            return byteArray;
        }
        RgbDatasMutex.unlock();
        av_usleep(10);
    }
}