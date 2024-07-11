//
// Created by Administrator on 2024-03-20.
//
#pragma once

#include <mutex>
#include <queue>
#include <unistd.h>

extern "C" {
#include "librtmp/rtmp.h"
}

class SafeQueue {
private:
    std::mutex mutex;
    std::queue<RTMPPacket *> packets;
public:
    void pushPacket(RTMPPacket *packet) {
        mutex.lock();
        packets.push(packet);
        mutex.unlock();
    }

    RTMPPacket *popPacket() {
        mutex.lock();
        if (!packets.empty()) {
            RTMPPacket *packet = packets.front();
            packets.pop();
            mutex.unlock();
            return packet;
        }
        mutex.unlock();
        return 0;
    }

    int size() {
        return packets.size();
    }

    void clear() {
        mutex.lock();
        while (!packets.empty()) {
            RTMPPacket *packet = packets.front();
            packets.pop();
            RTMPPacket_Free(packet);
            free(packet);
        }
        mutex.unlock();
    }
};
