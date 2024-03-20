//
// Created by Administrator on 2024-03-20.
//
#pragma once

#include <android/log.h>
#include "SafeQueue.cxx"
#include <thread>

extern "C" {
    int64_t av_gettime();
}

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_librtmp",FORMAT,##__VA_ARGS__)

static SafeQueue *queue = nullptr;

class LibRtmp {
private:
    bool isRunning;
    bool isExit[2] = {0};
    RTMP *rtmp;
    std::string url;

    void connectRTMP(const char *url) {
        rtmp = RTMP_Alloc();
        RTMP_Init(rtmp);
        rtmp->Link.timeout = 10;
        RTMP_SetupURL(rtmp, (char *) url);
        RTMP_EnableWrite(rtmp);
        RTMP_Connect(rtmp, 0);
        int re = RTMP_ConnectStream(rtmp, 0);
        if (!re) {
            RTMP_Close(rtmp);
            RTMP_Free(rtmp);
            LOGE("connect failed");
        }
        LOGE("connect success");
    }

    void sendPacket(RTMPPacket *packet) {
        int re = RTMP_SendPacket(rtmp, packet, 1);
        RTMPPacket_Free(packet);
        free(packet);
        if (!re) {
            RTMP_Close(rtmp);
            RTMP_Free(rtmp);
            LOGE("send packet failed");
            connectRTMP(url.c_str());//reconnect
        }
    }


    void mainThread() {
        while (isRunning) {
            RTMPPacket *packet = queue->popPacket();
            if (packet == 0) {
                continue;
            }
            LOGE("send packet type:%d", packet->m_packetType);
            //tms
            LOGE("send packet time:%d", packet->m_nTimeStamp);
            sendPacket(packet);
        }
        isExit[0] = true;
    }


public:
    LibRtmp(std::string url) {
        this->url = url;
        if (queue == nullptr) {
            queue = new SafeQueue();
        }
    }

    ~LibRtmp() {
        isRunning = false;
        while (!isExit[0]) {
            usleep(1);
        }
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        queue->clear();
        delete queue;
        queue = nullptr;
    }

    void start() {
        isRunning = true;
        isExit[0] = false;
        connectRTMP(url.c_str());
        std::thread t1(&LibRtmp::mainThread, this);
        t1.detach();
    }


};
