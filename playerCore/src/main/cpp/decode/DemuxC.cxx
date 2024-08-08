#pragma once

#include <../IObserverC.cxx>
#include <string>
#include <iostream>
#include <vector>
#include <thread>
#include "android/log.h"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)

#include "../LogC.cxx"

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
    LogC *mlog = 0;

    void demuxFile() {
        mlog->log("fileDemux: loading", mlog->getEnv());
        mlog->releaseEnv();
        while (isRunning) {
            AVPacket *packet = av_packet_alloc();
            int ret = av_read_frame(avFormatContext, packet);
            if (ret != 0) {
                av_packet_free(&packet);
                if (ret == AVERROR_EOF) {
                    mlog->log("fileDemux: finishLoad", mlog->getEnv());
                    mlog->releaseEnv();
                    break;
                } else if (ret == AVERROR(EAGAIN)) {
                    JNIEnv* env = mlog->getEnv();
                    mlog->log("fileDemux: try again", env);
                    mlog->releaseEnv();
                    continue;
                } else {
                    // Other errors
                    JNIEnv* env = mlog->getEnv();
                    mlog->log("fileDemux: Error reading frame: " + std::to_string(ret), env);
                    mlog->releaseEnv();
                    break;
                }
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
    DemuxC(std::string url, LogC *mlog, bool* callback) {
        this->mlog = mlog;
        mlog->log("DemuxC",mlog->getEnv());
        av_register_all();
        avcodec_register_all();
        avformat_network_init();


        mlog->log("demux_loading", mlog->getEnv());

        int re = avformat_open_input(&avFormatContext, url.c_str(), 0, 0);
        mlog->log(std::to_string(re), mlog->getEnv());

        if (re != 0) {
            mlog->log("demux_error", mlog->getEnv());
            char buf[1024] = {0};
            av_strerror(re, buf, 1024);
            mlog->log(buf, mlog->getEnv());
            if(callback != nullptr) *callback = false;//失败了回调 避免初始其他类
            return;
        }
        re = avformat_find_stream_info(avFormatContext, NULL);//get stream info such as video stream, audio stream info
        mlog->log(std::to_string(re), mlog->getEnv());

        totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
        mlog->log("demux_loaded", mlog->getEnv());
        if(callback != nullptr) *callback = true;
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

    void seekTo(int64_t pos) {
        if (pos < 0 || pos > totalMs) {
            return;
        }

        int videoStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);
        int audioStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);

        if (videoStreamIndex < 0 || audioStreamIndex < 0) {
            LOGE("Could not find video or audio stream.");
            return;
        }

        // Convert pos (in milliseconds) to time base units
        int64_t videoTimestamp = av_rescale_q(pos, AV_TIME_BASE_Q, avFormatContext->streams[videoStreamIndex]->time_base);
        int64_t audioTimestamp = av_rescale_q(pos, AV_TIME_BASE_Q, avFormatContext->streams[audioStreamIndex]->time_base);

        int re = av_seek_frame(avFormatContext, videoStreamIndex, videoTimestamp, AVSEEK_FLAG_BACKWARD);
        if (re < 0) {
            char buf[1024] = {0};
            av_strerror(re, buf, 1024);
            LOGE("seek video error: %s", buf);
        }

        re = av_seek_frame(avFormatContext, audioStreamIndex, audioTimestamp, AVSEEK_FLAG_BACKWARD);
        if (re < 0) {
            char buf[1024] = {0};
            av_strerror(re, buf, 1024);
            LOGE("seek audio error: %s", buf);
        }

        //clear the queue

    }


    int64_t getTotalMs() {
        return totalMs;
    }

    AVFormatContext *getAvFormatContext() {
        return avFormatContext;
    }


};