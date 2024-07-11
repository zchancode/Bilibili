//
// Created by Administrator on 2024-03-25.
//
#pragma once

#include <string>
#include <android/native_window.h>
#include "FFDemux.cxx"
#include "FFAudioDecode.cxx"
#include "FFVideoDecode.cxx"
#include "SLAudioPlayer.cxx"
#include "GLVideoPlayer.cxx"
class MainInterface {
private:
    std::string url;
    ANativeWindow *win;
    FFDemux *demux;
    FFAudioDecode *aDecode;
    FFVideoDecode *vDecode;
    SLAudioPlayer *slAudioPlayer;
    GLVideoPlayer *glVideoPlayer;

    public:
    MainInterface(std::string url, ANativeWindow *win){
        this->url = url;
        this->win = win;
    }
    void stopPlay(){

        LOGE("stopPlay");
        delete demux;
        delete aDecode;
        delete vDecode;
        delete slAudioPlayer;
        delete glVideoPlayer;

    }
    void startPlay(){
        demux = new FFDemux(url);
        aDecode = new FFAudioDecode(demux->getAVFormatContext());
        vDecode = new FFVideoDecode(demux->getAVFormatContext());

        slAudioPlayer = new SLAudioPlayer(aDecode->getCodecContext());
        glVideoPlayer = new GLVideoPlayer(win, slAudioPlayer);
        demux->addObserver(vDecode);
        demux->addObserver(aDecode);
        aDecode->addObserver(slAudioPlayer);
        vDecode->addObserver(glVideoPlayer);


        demux->start();
        aDecode->start();
        vDecode->start();
        glVideoPlayer->start();
        slAudioPlayer->start();

    }

};
