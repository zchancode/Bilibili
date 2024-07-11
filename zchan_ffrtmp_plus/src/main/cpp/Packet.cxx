//
// Created by Administrator on 2024-03-23.
//
#pragma once

#include <stdint.h>

class Packet{
    public:
    void *data[3] = {0};
    int line_size[3] = {0};
    int type = 0; // 0NV12 1I420
    int64_t pts = 0;


};
