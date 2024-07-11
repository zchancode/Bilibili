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
#include <libswresample/swresample.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"PUSH",FORMAT,##__VA_ARGS__);

class AudioEncoder {

private:
    AVFrame *pcm_frame;
    std::queue<Packet> audio_queue;
    std::mutex audio_mux;
    SwrContext *asc = nullptr;

    Packet encode(Packet pkt) {
        AVPacket *avPacket = av_packet_alloc();
        Packet ret_pkt;

        const uint8_t *indata[AV_NUM_DATA_POINTERS] = {0};
        indata[0] = (const uint8_t *) pkt.data[0];
        swr_convert(asc,
                    pcm_frame->data, pcm_frame->nb_samples,
                    indata, pcm_frame->nb_samples);
        pcm_frame->pts = pkt.pts;
        avcodec_send_frame(ac, pcm_frame);
        avcodec_receive_packet(ac, avPacket);
        ret_pkt.data[0] = avPacket;
        ret_pkt.type = 0; // audio
        free(pkt.data[0]);

        return ret_pkt;
    }


public:
    AVCodecContext *ac = nullptr;

    AudioEncoder() {
        asc = swr_alloc_set_opts(asc,
                                 av_get_default_channel_layout(2),
                                 AV_SAMPLE_FMT_FLTP,
                                 44100,
                                 av_get_default_channel_layout(2),
                                 AV_SAMPLE_FMT_S16,
                                 44100,
                                 0,
                                 nullptr);
        swr_init(asc);
        pcm_frame = av_frame_alloc();
        pcm_frame->format = AV_SAMPLE_FMT_FLTP;
        pcm_frame->channels = 2;
        pcm_frame->channel_layout = av_get_default_channel_layout(2);
        pcm_frame->nb_samples = 1024;//一帧音频单通道的采样数量
        pcm_frame->pts = 0;
        av_frame_get_buffer(pcm_frame, 0);
        AVCodec *audio_codec = avcodec_find_encoder(AV_CODEC_ID_AAC);
        ac = avcodec_alloc_context3(audio_codec);
        ac->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;
        ac->codec_id = audio_codec->id;
        ac->sample_fmt = AV_SAMPLE_FMT_FLTP;
        ac->sample_rate = 44100;
        ac->channel_layout = av_get_default_channel_layout(2);
        ac->channels = 2;
        ac->thread_count = 8;
        ac->bit_rate = 40000;
        ac->time_base = {1, 1000000};
        avcodec_open2(ac, audio_codec, nullptr);
        LOGE("AudioEncoder init success")
    }

    ~AudioEncoder() {
        while (!audio_queue.empty()) {
            Packet pkt = audio_queue.front();
            audio_queue.pop();
            free(pkt.data[0]);
        }
        avcodec_free_context(&ac);
        swr_free(&asc);
        av_frame_free(&pcm_frame);
    }

    Packet receive() {
        if (audio_queue.empty()) {
            return Packet();
        }
        audio_mux.lock();
        Packet pkt = audio_queue.front();
        audio_queue.pop();
        audio_mux.unlock();
        return encode(pkt);
    }

    void send(Packet pkt) {
        while (audio_queue.size() > 100) {
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        }
        audio_mux.lock();
        audio_queue.push(pkt);
        audio_mux.unlock();
    }


};