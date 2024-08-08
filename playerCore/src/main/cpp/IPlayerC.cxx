//
// Created by Administrator on 2024-03-18.
//
#pragma once

#include <android/native_window.h>
#include <android/native_window_jni.h>


#include "decode/DemuxC.cxx"
#include "decode/VideoDecodeC.cxx"
#include "video/VideoPlayerC.cxx"
#include "decode/AudioDecodeC.cxx"
#include "resample/AudioResampleC.cxx"
#include "audio/AudioPlayerC.cxx"
#include "LogC.cxx"

class IPlayerC {
private:
    std::string url;
    ANativeWindow *win;
    DemuxC *demux;
    AudioDecodeC *audioDecode;
    AudioResampleC *audioResample;
    AudioPlayerC *audioPlayer;
    VideoDecodeC *videoDecode;
    VideoPlayerC *videoPlayer;
    LogC *mlog = nullptr;

public:
    IPlayerC(std::string url, ANativeWindow *win, LogC *mlog) {
        this->url = url;
        this->win = win;
        this->mlog = mlog;


    }

    void startPlay() {
        demux = new DemuxC(url.c_str(), mlog, nullptr);

        audioDecode = new AudioDecodeC(demux->getAvFormatContext());
        audioResample = new AudioResampleC(audioDecode->getAVCodecContext());
        audioPlayer = new AudioPlayerC();
        demux->addObserver(audioDecode);
        audioDecode->addObserver(audioResample);
        audioResample->addObserver(audioPlayer);

        videoDecode = new VideoDecodeC(demux->getAvFormatContext(), audioPlayer, mlog);
        videoPlayer = new VideoPlayerC(win);
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


    void seekTo(int64_t pos) {
        demux->seekTo(pos);
    }
};