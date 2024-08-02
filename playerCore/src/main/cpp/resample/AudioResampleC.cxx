//
// Created by Administrator on 2024-07-29.
//

#pragma once

#include <../IObserverC.cxx>
#include <queue>
#include <mutex>
#include <thread>

extern "C" {
#include "libswresample/swresample.h"
#include "libavcodec/avcodec.h"
#include "libavutil/time.h"
}

class AudioResampleC : public IObserverC {

private:
    SwrContext *swrContext = nullptr;
    std::queue<AVFrame *> audioFrames;
    std::mutex mutex;
    bool isRunning = true;
    bool isExit = false;


    void threadMain() {
        while (isRunning) {
            if (audioFrames.empty()) {
                av_usleep(10);
                continue;
            }
            mutex.lock();
            AVFrame *audioFrame = audioFrames.front();
            audioFrames.pop();
            mutex.unlock();
            int pcmSize = (audioFrame->nb_samples *
                           av_get_bytes_per_sample(AV_SAMPLE_FMT_S16) *
                           audioFrame->channels);
            uint8_t *pcm_buffer = (uint8_t *) malloc(sizeof(uint8_t) * pcmSize);
            uint8_t *out_buffer[2] = {0x00};
            out_buffer[0] = pcm_buffer;
            swr_convert(swrContext,
                        out_buffer,
                        audioFrame->nb_samples,
                        (const uint8_t **) audioFrame->data,
                        audioFrame->nb_samples);
            DataC pcmData;
            pcmData.data = pcm_buffer;
            pcmData.size = pcmSize;
            pcmData.pts = audioFrame->pts;
            av_frame_free(&audioFrame);
            sendData(pcmData);
        }
        isExit = true;
    }

public:
    AudioResampleC(AVCodecContext *a_codec) {
        swrContext = swr_alloc();
        swrContext = swr_alloc_set_opts(swrContext,
                //out
                                        av_get_default_channel_layout(2),
                                        AV_SAMPLE_FMT_S16,
                                        a_codec->sample_rate,//44100
                //in
                                        av_get_default_channel_layout(a_codec->channels),
                                        a_codec->sample_fmt,
                                        a_codec->sample_rate,
                                        0, 0);
        swr_init(swrContext);
    }

    ~AudioResampleC() {
        isRunning = false;
        while (!isExit) {
            av_usleep(10);
        }
        swr_free(&swrContext);
        while (!audioFrames.empty()) {
            AVFrame *frame = audioFrames.front();
            audioFrames.pop();
            av_frame_free(&frame);
        }
    }

    void receiveData(DataC data) override {
        while (audioFrames.size() > 100) {
            av_usleep(10);
        }
        mutex.lock();
        audioFrames.push((AVFrame *) data.data);
        mutex.unlock();
    }

    void start() {
        std::thread t(&AudioResampleC::threadMain, this);
        t.detach();
    }

};
