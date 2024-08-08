//
// Created by Administrator on 2024-07-29.
//

#pragma once
#include <android/log.h>
#include "ShaderC.cxx"
#include "EGLC.cxx"
#include <android/native_window.h>

extern "C" {
#include "libavcodec/avcodec.h"
}

#include <../IObserverC.cxx>
class VideoPlayerC : public IObserverC {
private:
    ShaderC *shader = nullptr;
    EGLC *egl = nullptr;
    ANativeWindow *win = nullptr;

public:
    VideoPlayerC(ANativeWindow *win) {
        this->win = win;
    }

    ~VideoPlayerC() {
        LOGE("VideoPlayer release start");
        if (shader) {
            delete shader;
            shader = nullptr;
        }
        if (egl) {
            delete egl;
            egl = nullptr;
        }
        LOGE("VideoPlayer release");
    }

    void receiveData(DataC data) override {
        AVFrame *frame = (AVFrame *) data.data;
        if (!frame->data[0]) { //不能有空帧 有空帧，后面的数据不是空帧也不会显示
            return;
        }

        if (!egl) {//一定要在这里初始化，渲染线程和主线程要在同一个线程，在同一个函数里面
            LOGE("Egl");
            egl = new EGLC(win);
            shader = new ShaderC();
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
//            glClearColor(.2, .2, .2, 1.0);
            glClear(GL_COLOR_BUFFER_BIT);
            glViewport((w - widthDisplay) / 2, (h - heightDisplay) / 2, widthDisplay, heightDisplay);

        }

        shader->createTexture(0, frame->width, frame->height, frame->data[0]);
        shader->createTexture(1, frame->width / 2, frame->height / 2, frame->data[1]);
        shader->createTexture(2, frame->width / 2, frame->height / 2, frame->data[2]);
        shader->draw();
        egl->swapBuffers();


    }

};
