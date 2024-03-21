//
// Created by Administrator on 2024-03-18.
//
#pragma once

#include <IObserver.cxx>
#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)

#include <queue>
#include <mutex>

extern "C" {
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <libavutil/time.h>
}


class AudioPlayer : public IObserver {
private:
    bool isRunning = true;
    uint8_t *cacheFrame = nullptr;
    std::queue<Data> pcmFrames;
    std::mutex pcmFramesMutex;
    SLObjectItf playObj = nullptr;
    SLObjectItf engineObject = nullptr;
    SLEngineItf engineEngine = nullptr;
    SLPlayItf playItf = nullptr;
    SLObjectItf outputMixObject = nullptr;
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = nullptr;
    int64_t aPts = 0;

    void playCall() {
        while (isRunning) {
            if (pcmFrames.empty()) {
                av_usleep(10);
                continue;
            }

            pcmFramesMutex.lock();
            Data data = pcmFrames.front();
            pcmFrames.pop();
            pcmFramesMutex.unlock();
            aPts = data.pts;
            memcpy(cacheFrame, data.data, data.size);
            (*pcmBufferQueue)->Enqueue(pcmBufferQueue, cacheFrame, data.size);
            free(data.data);
            break;
        }
    }

    static void pcmCall(SLAndroidSimpleBufferQueueItf bf, void *context) {
        ((AudioPlayer *) context)->playCall();
    }

public:
    AudioPlayer() {
        isRunning = true;
        cacheFrame = (uint8_t *) malloc(sizeof(uint8_t) * 1024 * 2 * 2);
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
        (*pcmBufferQueue)->Enqueue(pcmBufferQueue, "", 1);
    }

    ~AudioPlayer() {
        LOGE("AudioPlayer release start");
        isRunning = false;
        (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_STOPPED);

        if (playObj) {
            (*playObj)->Destroy(playObj);
            playObj = nullptr;
            playItf = nullptr;
        }
        if (outputMixObject) {
            (*outputMixObject)->Destroy(outputMixObject);
            outputMixObject = nullptr;
        }
        if (engineObject) {
            (*engineObject)->Destroy(engineObject);
            engineObject = nullptr;
            engineEngine = nullptr;
        }
        if (cacheFrame) {
            free(cacheFrame);
            cacheFrame = nullptr;
        }
        while (!pcmFrames.empty()) {
            Data data = pcmFrames.front();
            pcmFrames.pop();
            if (data.data) {
                free(data.data);
            }
        }
        aPts = 0;
        LOGE("AudioPlayer release");
    }

    int64_t getAPts() {
        return aPts;
    }

    void receiveData(Data data) override {
        LOGE("receiveData %d", pcmFrames.size());
        while (pcmFrames.size() > 100) {
            av_usleep(10);
        }
        pcmFramesMutex.lock();
        pcmFrames.push(data);
        pcmFramesMutex.unlock();
    }
};

