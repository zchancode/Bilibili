#pragma once


extern "C" {
#include "libswscale/swscale.h"
#include "libavutil/frame.h"

}

#include "X264Encoder.cxx"

class SwsVideo {
private:
    SwsContext *vsc;
    int width;
    int height;
    X264Encode *x264Encoder = nullptr;

public:
    SwsVideo(int width, int height, X264Encode *x264Encoder) {
        this->width = width;
        this->height = height;
        this->x264Encoder = x264Encoder;
        vsc = sws_getContext(width, height, AV_PIX_FMT_RGBA,
                             width, height, AV_PIX_FMT_YUV420P,
                             SWS_BICUBIC, 0, 0, 0);

    }

    ~SwsVideo() {
        sws_freeContext(vsc);
    }

    void encodeRGB(uint8_t *rgb) {
        uint8_t *indata[AV_NUM_DATA_POINTERS] = {0};
        indata[0] = rgb;
        int insize[AV_NUM_DATA_POINTERS] = {0};
        insize[0] = width * 4;

        uint8_t *yuv_data[3] = {0};
        yuv_data[0] = (uint8_t *) malloc(width * height);
        yuv_data[1] = (uint8_t *) malloc(width * height / 4);
        yuv_data[2] = (uint8_t *) malloc(width * height / 4);
        uint8_t *outdata[AV_NUM_DATA_POINTERS] = {0};
        outdata[0] = yuv_data[0];
        outdata[1] = yuv_data[1];
        outdata[2] = yuv_data[2];
        int outsize[AV_NUM_DATA_POINTERS] = {0};
        outsize[0] = width;
        outsize[1] = width / 2;
        outsize[2] = width / 2;
        sws_scale(vsc, indata, insize, 0, height, outdata, outsize);
        x264Encoder->encodeI420((int8_t *) yuv_data[0], (int8_t *) yuv_data[1],
                                (int8_t *) yuv_data[2]);
        free(yuv_data[0]);
        free(yuv_data[1]);
        free(yuv_data[2]);
    }

};