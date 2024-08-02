#pragma once

#include <../IObserverC.cxx>
#include <string>
#include <iostream>
#include <vector>
#include <thread>
#include "android/log.h"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)


extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}


class DemuxC : public IObserverC {
private:
    AVFormatContext *avFormatContext = 0;
    int64_t totalMs = 0;

    double r2d(AVRational r) {
        return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
    }

    bool isRunning = true;
    bool isExit = false;

    void demuxFile() {
        while (isRunning) {
            AVPacket *packet = av_packet_alloc();
            int ret = av_read_frame(avFormatContext, packet);
            if (ret != 0) {
                av_packet_free(&packet);
                break;
            }
            packet->pts = packet->pts *
                          (r2d(avFormatContext->streams[packet->stream_index]->time_base) * 1000);
            DataC data;
            data.data = packet;
            sendData(data); //block
        }
        isExit = true;
    }

public:
    DemuxC(std::string url) {
        av_register_all();
        avcodec_register_all();
        avformat_network_init();

        avformat_open_input(&avFormatContext, url.c_str(), 0, 0);//open mp4 file
        avformat_find_stream_info(avFormatContext,
                                  NULL);//get stream info such as video stream, audio stream info
        totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
    }

    ~DemuxC() {
        isRunning = false;
        while (!isExit) {
            av_usleep(10);
        }
        avformat_close_input(&avFormatContext);
        totalMs = 0;
    }

    void start() {
        std::thread t(&DemuxC::demuxFile, this);
        t.detach();
    }

    int64_t getTotalMs() {
        return totalMs;
    }

    AVFormatContext *getAvFormatContext() {
        return avFormatContext;
    }


};