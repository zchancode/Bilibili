//
// Created by Administrator on 2024-03-23.
//
#pragma once

#include <mutex>
#include <queue>
extern "C" {
#include "libavcodec/avcodec.h"
}
class PushQueue {
private:
    std::mutex mux;
    std::queue<AVPacket *> packets;
public:
    void pushPacket(AVPacket * packet) {
        mux.lock();
        packets.push(packet);
        mux.unlock();
    }

    AVPacket * popPacket() {
        AVPacket * packet = nullptr;
        mux.lock();
        if (!packets.empty()) {
            packet = packets.front();
            packets.pop();
            mux.unlock();
            return packet;
        }
        mux.unlock();
        return packet;
    }

    int size() {
        return packets.size();
    }

};
