//
// Created by Administrator on 2024-03-26.
//

#pragma once

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <malloc.h>
#include <string.h>

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_player_annotating",FORMAT,##__VA_ARGS__)

#import "FFObserver.cxx"
#include <queue>
#include <mutex>
#include <thread>
#include "FFData.cxx"

extern "C" {
#include <libavutil/time.h>
#include "libswresample/swresample.h"
#include "libavcodec/avcodec.h"


}

class SLAudioPlayer : public FFObserver {
private:
    int64_t aPts = 0;
    SLObjectItf playObj = nullptr;
    SLObjectItf engineObject = nullptr;
    SLEngineItf engineEngine = nullptr;
    SLPlayItf playItf = nullptr;
    SLObjectItf outputMixObject = nullptr;
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = nullptr;

    SwrContext *swrContext = nullptr;
    bool isRunning = true;

    std::queue<FFData> queue;
    std::mutex mutex;
    uint8_t *cacheFrame = nullptr;

    void playCall() {
        while (isRunning) {
            if (queue.empty() ) {
                av_usleep(1);
                continue;
            }
            mutex.lock();
            FFData data = queue.front();
            queue.pop();
            mutex.unlock();
            AVFrame *frame = (AVFrame *) data.p[0];
            uint8_t *out_buffer[2] = {0};
            out_buffer[0] = cacheFrame;
            swr_convert(swrContext,
                        out_buffer, frame->nb_samples,
                        (const uint8_t **) frame->data, frame->nb_samples);
            aPts = frame->pts;
            (*pcmBufferQueue)->Enqueue(pcmBufferQueue, cacheFrame, frame->nb_samples * 2 * 2);
            av_frame_free(&frame);
            break;
        }


    }

    static void pcmCall(SLAndroidSimpleBufferQueueItf bf, void *context) {
        ((SLAudioPlayer *) context)->playCall();
    }

public:
    SLAudioPlayer(AVCodecContext *aCodecContext) {
        cacheFrame = (uint8_t *) malloc(1024 * 16 * 2); //just a cache size is big enough
        swrContext = swr_alloc();
        swrContext = swr_alloc_set_opts(swrContext,
                //out
                                        av_get_default_channel_layout(2),
                                        AV_SAMPLE_FMT_S16,
                                        aCodecContext->sample_rate,
                //in
                                        av_get_default_channel_layout(aCodecContext->channels),
                                        aCodecContext->sample_fmt,
                                        aCodecContext->sample_rate,
                                        0, 0);
        swr_init(swrContext);


        slCreateEngine(&engineObject, 0, 0, 0, 0, 0);
        (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
        (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE,
                                      &engineEngine);//1.get engine interface
        (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, 0, 0);
        (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
        SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
        SLDataSink audioSink = {&outputMix, 0};
        SLDataLocator_AndroidSimpleBufferQueue androidQueue = {
                SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                2};
        SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM,
                                2,
                                SL_SAMPLINGRATE_44_1,
                                SL_PCMSAMPLEFORMAT_FIXED_16,
                                SL_PCMSAMPLEFORMAT_FIXED_16,
                                SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                                SL_BYTEORDER_LITTLEENDIAN};
        SLDataSource slDataSource = {&androidQueue, &pcm};
        const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
        const SLboolean req[] = {SL_BOOLEAN_TRUE};
        (*engineEngine)->CreateAudioPlayer(engineEngine, &playObj, &slDataSource, &audioSink, 1,
                                           ids, req);
        (*playObj)->Realize(playObj, SL_BOOLEAN_FALSE);
        (*playObj)->GetInterface(playObj, SL_IID_PLAY, &playItf);
        (*playObj)->GetInterface(playObj, SL_IID_BUFFERQUEUE, &pcmBufferQueue);
        (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmCall, this);
        (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);
    }

    ~SLAudioPlayer() {
        isRunning = false;

        (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_STOPPED);
        (*playObj)->Destroy(playObj);
        playObj = nullptr;
        playItf = nullptr;
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = nullptr;
        (*engineObject)->Destroy(engineObject);

        engineObject = nullptr;
        engineEngine = nullptr;

        free(cacheFrame);
        cacheFrame = nullptr;

        swr_free(&swrContext);
        swrContext = nullptr;

        while (!queue.empty()) {
            FFData data = queue.front();
            queue.pop();
            AVFrame *frame = (AVFrame *) data.p[0];
            av_frame_free(&frame);
        }
        LOGE("~SLAudioPlayer()");
        isRunning = true;


    }

    void receive(FFData data) {
        LOGE("receive audio data %d", queue.size());
        while (queue.size() > 100) {
            av_usleep(1);
        }
        mutex.lock();
        queue.push(data);
        mutex.unlock();
    }

    int64_t getPts() {
        return aPts;
    }

    void start() {
        std::thread t(&SLAudioPlayer::playCall, this);
        t.detach();
    }

};
