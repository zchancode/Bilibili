//
// Created by Administrator on 2024-03-26.
//

#pragma once
extern "C" {
#include "libavutil/time.h"
#include "libavformat/avformat.h"
}

#include "FFObserver.cxx"
#include <queue>
#include <mutex>
#include <thread>

#include "android/log.h"
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_player_annotating",FORMAT,##__VA_ARGS__)
class FFAudioDecode : public FFObserver {
private:
    int streamIndex = -1;
    AVCodecContext *codecContext = nullptr;
    std::mutex mutex;
    std::queue<FFData> queue;
    bool isRunning = true;
    bool isExit = false;

    void mainThread() {
        while (isRunning) {
            if (queue.empty()) {
                av_usleep(1);
                continue;
            }
            mutex.lock();
            FFData data = queue.front();
            queue.pop();
            mutex.unlock();
            AVPacket *packet = (AVPacket *) data.p[0];
            avcodec_send_packet(codecContext, packet);
            av_packet_free(&packet);
            while (true) {
                AVFrame *frame = av_frame_alloc();
                int re = avcodec_receive_frame(codecContext, frame);
                if (re != 0) break;
                FFData d;
                d.p[0] = frame;
                send(d);
            }
        }
        isExit = true;
    }

public:
    FFAudioDecode(AVFormatContext *avFormatContext) {
        //判断是音频还是视频还可以用遍历流获得引索
        //you can also use stream traversal to determine whether it's audio or video
        for (int i = 0; i < avFormatContext->nb_streams; i++) {
            AVStream *stream = avFormatContext->streams[i];
            if (stream->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
                LOGE("audio stream index %d", i);
                break;
            } else if (stream->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
                LOGE("video stream index %d", i);
                break;
            }
        }

        streamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
        AVCodecParameters *codecParameters = avFormatContext->streams[streamIndex]->codecpar;
        AVCodec *codec = avcodec_find_decoder(codecParameters->codec_id);
        codecContext = avcodec_alloc_context3(codec);
        avcodec_parameters_to_context(codecContext, codecParameters);
        avcodec_open2(codecContext, codec, nullptr);
    }

    ~FFAudioDecode() {
        isRunning = false;
        while (!isExit) {
            av_usleep(1);
        }
        LOGE("~FFAudioDecode()");
        avcodec_free_context(&codecContext);
        while (!queue.empty()) {
            FFData data = queue.front();
            queue.pop();
            AVPacket *packet = (AVPacket *) data.p[0];
            av_packet_free(&packet);
        }
        isRunning = true;
        isExit = false;
    }

    AVCodecContext *getCodecContext() {
        return codecContext;
    }

    void start() {
        std::thread t(&FFAudioDecode::mainThread, this);
        t.detach();
    }

    void receive(FFData data) override {
        if (data.dataType != streamIndex) return;
        while (queue.size() > 100) {
            av_usleep(1);
        }
        mutex.lock();
        queue.push(data);
        mutex.unlock();
    }


};
