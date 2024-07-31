//
// Created by Administrator on 2024-07-29.
//
#pragma once

#include "../IObserverC.cxx"
#include "android/log.h"
#include <queue>
#include "thread"

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}
class AudioDecodeC: public IObserverC {
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
                AVFrame *audioFrame = av_frame_clone(this->frame);
                DataC data;
                data.data = audioFrame;
                data.pts = packet->pts;
                sendData(data);
            }
            av_packet_free(&packet);
        }
        isExit = true;
    }

public:
    AudioDecodeC(AVFormatContext *avFormatContext) {
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


    ~AudioDecodeC() {
        isRunning = false;
        while (!isExit) {
            av_usleep(10);
        }
        avcodec_close(a_codec);
        avcodec_free_context(&a_codec);
        av_frame_free(&frame);

        while (!audioPackets.empty()) {
            AVPacket *packet = audioPackets.front();
            audioPackets.pop();
            av_packet_free(&packet);
        }
    }

    void start() {
        std::thread t(&AudioDecodeC::threadMain, this);
        t.detach();
    }

    AVCodecContext* getAVCodecContext() {
        return this->a_codec;
    }

    void stop() {
        isRunning = false;
    }

    void receiveData(DataC data) override {
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