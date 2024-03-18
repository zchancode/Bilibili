//
// Created by Administrator on 2024-03-17.
//
#include <IObserver.cxx>
#include "android/log.h"
#include <queue>
#include "thread"

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)

class AudioDecode : public IObserver {
private:
    int audioStreamIndex;
    AVCodecParameters *a_para;
    AVCodecContext *a_codec;
    std::queue<AVPacket *> audioPackets;
    std::mutex mutex;
    AVFrame *frame = nullptr;
    bool isRunning = true;
    bool isExit = false;

    void threadMain() {
        while (isRunning) {
            if (audioPackets.empty()) {
                av_usleep(10);
                continue;
            }
            mutex.lock();
            AVPacket *packet = audioPackets.front();
            audioPackets.pop();
            mutex.unlock();
            avcodec_send_packet(a_codec, packet);
            while (avcodec_receive_frame(a_codec, frame) == 0) {
                LOGE("audio pts %lld", frame->pts);
            }
            av_packet_free(&packet);
        }
        isExit = true;
    }

public:
    AudioDecode(AVFormatContext *avFormatContext) {
        isRunning = true;
        isExit = false;
        frame = av_frame_alloc();
        audioStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
        a_para = avFormatContext->streams[audioStreamIndex]->codecpar;
        AVCodec *ad = avcodec_find_decoder(a_para->codec_id);
        a_codec = avcodec_alloc_context3(ad);
        avcodec_parameters_to_context(a_codec, a_para);
        a_codec->thread_count = 8;
        avcodec_open2(a_codec, 0, 0);
    }

    ~AudioDecode() {
        isRunning = false;
        while (!isExit) {
            av_usleep(10);
        }
        avcodec_close(a_codec);
        avcodec_free_context(&a_codec);
        av_frame_free(&frame);
    }

    void start() {
        std::thread t(&AudioDecode::threadMain, this);
        t.detach();
    }

    void stop() {
        isRunning = false;
    }

    void receiveData(Data data) override {
        while (audioPackets.size() > 100) { //block the thread
            av_usleep(10);
        }
        AVPacket *packet = (AVPacket *) data.data;

        if (packet->stream_index == audioStreamIndex && packet->data != nullptr) {
            mutex.lock();
            audioPackets.push(packet);
            mutex.unlock();
        }

    }

};