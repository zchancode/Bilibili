#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_player_handwrite",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1player_1handwrite_JniImp_##name

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
#include "libavcodec/jni.h"
}

extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *res) {
    av_jni_set_java_vm(vm, nullptr);
    return JNI_VERSION_1_6;
}

JNI_FUNC(init)(JNIEnv *env, jclass thiz) {
    LOGE("init");
    avformat_network_init();
    AVFormatContext *avFormatContext = nullptr;
    avformat_open_input(&avFormatContext, "/sdcard/input.mp4", 0, 0);
    AVPacket *packet = av_packet_alloc();

    int audioIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
    int videoIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);


    AVCodec *audioCodec = avcodec_find_decoder(avFormatContext->streams[audioIndex]->codecpar->codec_id);
    AVCodec *videoCodec = avcodec_find_decoder_by_name("h264_mediacodec");
    AVCodecContext *videoCodecContext = avcodec_alloc_context3(videoCodec);
    avcodec_parameters_to_context(videoCodecContext,avFormatContext->streams[videoIndex]->codecpar);
    int re =  avcodec_open2(videoCodecContext, 0, 0);
    if (re != 0) {
        LOGE("avcodec_open2 failed");
        //reason
        char buf[1024] = {0};
        av_strerror(re, buf, sizeof(buf));
        LOGE("error buf is %s", buf);

        return;
    }
    LOGE("avcodec_open2 success");


    while (false) {
        int re = av_read_frame(avFormatContext, packet);
        if (re < 0) { //EOF
            break;
        }


    }
}