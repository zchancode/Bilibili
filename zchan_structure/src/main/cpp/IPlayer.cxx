//
// Created by Administrator on 2024-03-18.
//
#pragma once

#include <string>
#include "AudioDecode.cxx"
#include "VideoDecode.cxx"
#include "AudioResample.cxx"
#include "AudioPlayer.cxx"
#include "VideoPlayer.cxx"
#include "Demux.cxx"

class IPlayer {
private:
    std::string url;
    ANativeWindow *win;
    Demux *demux;
    AudioDecode *audioDecode;
    AudioResample *audioResample;
    AudioPlayer *audioPlayer;
    VideoDecode *videoDecode;
    VideoPlayer *videoPlayer;
public:
    IPlayer(std::string url, ANativeWindow *win) {
        this->url = url;
        this->win = win;
    }

    void startPlay() {
        demux = new Demux(url.c_str());
        audioDecode = new AudioDecode(demux->getAvFormatContext());
        audioResample = new AudioResample(audioDecode->getAVCodecContext());
        audioPlayer = new AudioPlayer();
        demux->addObserver(audioDecode);
        audioDecode->addObserver(audioResample);
        audioResample->addObserver(audioPlayer);

        videoDecode = new VideoDecode(demux->getAvFormatContext(), audioPlayer);
        videoPlayer = new VideoPlayer(win);
        demux->addObserver(videoDecode);
        videoDecode->addObserver(videoPlayer);

        demux->start();
        audioDecode->start();
        audioResample->start();
        videoDecode->start();
    }

    void stopPlay() {

        delete demux;
        delete videoDecode;
        delete audioDecode;
        delete audioResample;
        delete audioPlayer;
        delete videoPlayer;
    }
};