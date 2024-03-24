//
// Created by Administrator on 2024-03-23.
//
#pragma once

#include <mutex>
#include <queue>
#include <thread>
#include "Packet.cxx"

extern "C" {
#include <libavutil/avutil.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
}

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"PUSH",FORMAT,##__VA_ARGS__);

class VideoEncoder {

private:
    AVFrame *yuv_frame;
    std::queue<Packet> video_queue;
    std::mutex video_mux;
    SwsContext *vsc;

    Packet encodeNV12(Packet pkt) {
        LOGE("encodeNV12")
        AVPacket *avPacket = av_packet_alloc();
        Packet ret_pkt;
        int8_t *y_data = (int8_t *) pkt.data[0];
        int8_t *uv_data = (int8_t *) pkt.data[1];

        //NV12 -> I420
        int8_t *u_data = (int8_t *) malloc(yuv_frame->width * yuv_frame->height / 4);
        int8_t *v_data = (int8_t *) malloc(yuv_frame->width * yuv_frame->height / 4);
        for (int i = 0; i < yuv_frame->width * yuv_frame->height / 4; i++) {
            u_data[i] = uv_data[i * 2];
            v_data[i] = uv_data[i * 2 + 1];
        }
        memcpy(yuv_frame->data[0], y_data, yuv_frame->width * yuv_frame->height);
        memcpy(yuv_frame->data[1], u_data, yuv_frame->width * yuv_frame->height / 4);
        memcpy(yuv_frame->data[2], v_data, yuv_frame->width * yuv_frame->height / 4);
        yuv_frame->pts = pkt.pts;
        avcodec_send_frame(vc, yuv_frame);
        avcodec_receive_packet(vc, avPacket);
        ret_pkt.data[0] = avPacket;
        free(pkt.data[0]);
        free(pkt.data[1]);
        free(u_data);
        free(v_data);

        return ret_pkt;
    }

    Packet encodeI420(Packet pkt) {
        LOGE("encodeI420")
        AVPacket *avPacket = av_packet_alloc();
        Packet ret_pkt;
        int8_t *y_data = (int8_t *) pkt.data[0];
        int8_t *u_data = (int8_t *) pkt.data[1];
        int8_t *v_data = (int8_t *) pkt.data[2];
        memcpy(yuv_frame->data[0], y_data, yuv_frame->width * yuv_frame->height);
        memcpy(yuv_frame->data[1], u_data, yuv_frame->width * yuv_frame->height / 4);
        memcpy(yuv_frame->data[2], v_data, yuv_frame->width * yuv_frame->height / 4);
        yuv_frame->pts = pkt.pts;
        avcodec_send_frame(vc, yuv_frame);
        avcodec_receive_packet(vc, avPacket);
        ret_pkt.data[0] = avPacket;
        free(pkt.data[0]);
        free(pkt.data[1]);
        free(pkt.data[2]);
        return ret_pkt;
    }

    Packet encodeRGB(Packet pkt) {
        LOGE("encodeRGB")
        AVPacket *avPacket = av_packet_alloc();
        Packet ret_pkt;
        //RGB -> I420
        uint8_t *indata[AV_NUM_DATA_POINTERS] = {0};
        indata[0] = (uint8_t *) pkt.data[0];
        int insize[AV_NUM_DATA_POINTERS] = {0};
        insize[0] = yuv_frame->width * 4;

        uint8_t *yuv_data[3] = {0};
        yuv_data[0] = (uint8_t *) malloc(yuv_frame->width * yuv_frame->height);
        yuv_data[1] = (uint8_t *) malloc(yuv_frame->width * yuv_frame->height / 4);
        yuv_data[2] = (uint8_t *) malloc(yuv_frame->width * yuv_frame->height / 4);
        uint8_t *outdata[AV_NUM_DATA_POINTERS] = {0};
        outdata[0] = yuv_data[0];
        outdata[1] = yuv_data[1];
        outdata[2] = yuv_data[2];
        int outsize[AV_NUM_DATA_POINTERS] = {0};
        outsize[0] = yuv_frame->width;
        outsize[1] = yuv_frame->width / 2;
        outsize[2] = yuv_frame->width / 2;
        sws_scale(vsc, indata, insize, 0, yuv_frame->height, outdata, outsize);

        memcpy(yuv_frame->data[0], yuv_data[0], yuv_frame->width * yuv_frame->height);
        memcpy(yuv_frame->data[1], yuv_data[1], yuv_frame->width * yuv_frame->height / 4);
        memcpy(yuv_frame->data[2], yuv_data[2], yuv_frame->width * yuv_frame->height / 4);
        yuv_frame->pts = pkt.pts;
        avcodec_send_frame(vc, yuv_frame);
        avcodec_receive_packet(vc, avPacket);
        ret_pkt.data[0] = avPacket;
        free(pkt.data[0]);
        free(yuv_data[0]);
        free(yuv_data[1]);
        free(yuv_data[2]);

        return ret_pkt;
    }


public:
    AVCodecContext *vc;

    VideoEncoder(int width, int height) {
        yuv_frame = av_frame_alloc();
        yuv_frame->format = AV_PIX_FMT_YUV420P;
        yuv_frame->width = width;
        yuv_frame->height = height;
        yuv_frame->pts = 0;
        av_frame_get_buffer(yuv_frame, 32);

        vsc = sws_getContext(width, height, AV_PIX_FMT_RGBA,
                             width, height, AV_PIX_FMT_YUV420P,
                             SWS_BICUBIC, 0, 0, 0);

        AVCodec *video_codec = avcodec_find_encoder(AV_CODEC_ID_H264);
        vc = avcodec_alloc_context3(video_codec);
        vc->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
        vc->codec_id = video_codec->id;
        vc->thread_count = 8;
        vc->bit_rate = 200000 * 1024 * 8;
        vc->width = width;
        vc->height = height;
        vc->time_base = {1, 1000000};
        vc->framerate = {33, 1};
        vc->gop_size = 25;
        vc->max_b_frames = 0;
        vc->pix_fmt = AV_PIX_FMT_YUV420P;
        avcodec_open2(vc, video_codec, 0);
        LOGE("VideoEncoder init success")

    }

    ~VideoEncoder() {
        while (!video_queue.empty()) {
            Packet pkt = video_queue.front();
            video_queue.pop();
            free(pkt.data[0]);
            free(pkt.data[1]);
        }
        av_frame_free(&yuv_frame);
        avcodec_free_context(&vc);
        sws_freeContext(vsc);

    }

    Packet receive() {
        if (video_queue.empty()) {
            return Packet();
        }
        video_mux.lock();
        Packet pkt = video_queue.front();
        video_queue.pop();
        video_mux.unlock();
        if (pkt.type == 0)//NV12
            return encodeNV12(pkt);
        else if (pkt.type == 1)//YUV420P
            return encodeI420(pkt);
        else if (pkt.type == 2)//RGB
            return encodeRGB(pkt);
    }

    void send(Packet pkt) {
        while (video_queue.size() > 100) {
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        }
        video_mux.lock();
        video_queue.push(pkt);
        video_mux.unlock();
    }


};