//
// Created by Administrator on 2024-03-26.
//

#pragma once
extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}

#import "FFObserver.cxx"
#include <queue>
#include <mutex>
#include <thread>
#include <android/native_window.h>
#include "AndroidEGL.cxx"
#include "GLShader.cxx"
#include "SLAudioPlayer.cxx"

class GLVideoPlayer : public FFObserver {
private:
    std::mutex mutex;
    std::queue<FFData> queue;
    bool isRunning = true;
    bool isExit = false;
    ANativeWindow *win = nullptr;
    AndroidEGL *egl = nullptr;
    GLShader *shader = nullptr;
    int64_t vPts = 0;
    SLAudioPlayer *audioPlayer = nullptr;

    void mainThread() {
        while (isRunning) {

            if (queue.empty() ||
                (vPts > audioPlayer->getPts() && vPts > 0 && audioPlayer->getPts() > 0)) {
                av_usleep(1);
                continue;
            }

            mutex.lock();
            FFData data = queue.front();
            queue.pop();
            mutex.unlock();
            AVFrame *frame = (AVFrame *) data.p[0];
            vPts = frame->pts;

            if (!egl) {//一定要在这里初始化，渲染线程和主线程要在同一个线程，在同一个函数里面
                LOGE("Egl");
                egl = new AndroidEGL(win);
                shader = new GLShader();
                int w = ANativeWindow_getWidth(win);
                int h = ANativeWindow_getHeight(win);
                //place into the center of window
                double r = frame->width / (double) frame->height;
                int widthDisplay = w;
                int heightDisplay = w / r;
                if (heightDisplay > h) {
                    heightDisplay = h;
                    widthDisplay = h * r;
                }
                glClearColor(.2, .2, .2, 1.0);
                glClear(GL_COLOR_BUFFER_BIT);
                glViewport((w - widthDisplay) / 2, (h - heightDisplay) / 2, widthDisplay,
                           heightDisplay);

            }

            shader->createTexture(0, frame->width, frame->height, frame->data[0]);
            shader->createTexture(1, frame->width / 2, frame->height / 2, frame->data[1]);
            shader->createTexture(2, frame->width / 2, frame->height / 2, frame->data[2]);
            shader->draw();
            egl->swapBuffers();
            av_frame_free(&frame);
        }
        isExit = true;
    }

public:
    GLVideoPlayer(ANativeWindow *win, SLAudioPlayer *audioPlayer) {
        this->win = win;
        this->audioPlayer = audioPlayer;
    }

    ~GLVideoPlayer() {
        isRunning = false;
        while (!isExit) {
            av_usleep(1);
        }
        LOGE("~GLVideoPlayer()");

        delete egl;
        egl = nullptr;
        delete shader;
        shader = nullptr;

        while (queue.size() > 0) {
            FFData data = queue.front();
            queue.pop();
            AVFrame *frame = (AVFrame *) data.p[0];
            av_frame_free(&frame);
        }

        isRunning = true;
        isExit = false;
    }

    void receive(FFData data) override {
        while (queue.size() > 100) {
            av_usleep(1);
        }
        mutex.lock();
        queue.push(data);
        mutex.unlock();
    }

    void start() {
        std::thread t(&GLVideoPlayer::mainThread, this);
        t.detach();
    }
};
