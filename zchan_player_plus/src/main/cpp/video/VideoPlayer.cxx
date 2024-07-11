//
// Created by Administrator on 2024-03-18.
//
#pragma once

#include <IObserver.cxx>
#include <android/log.h>
#include <Shader.cxx>
#include <EGL.cxx>
#include <android/native_window.h>

extern "C" {
#include "libavcodec/avcodec.h"
}
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)

class VideoPlayer : public IObserver {
private:
    Shader *shader = nullptr;
    EGL *egl = nullptr;
    ANativeWindow *win = nullptr;

public:
    VideoPlayer(ANativeWindow *win) {
        this->win = win;
    }

    ~VideoPlayer() {
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
    void receiveData(Data data) override {
        AVFrame *frame = (AVFrame *) data.data;
        if (!frame->data[0]) { //不能有空帧 有空帧，后面的数据不是空帧也不会显示
            return;
        }

        if (!egl) {//一定要在这里初始化，渲染线程和主线程要在同一个线程，在同一个函数里面
            LOGE("Egl");
            egl = new EGL(win);
            shader = new Shader();
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
            glViewport((w - widthDisplay) / 2, (h - heightDisplay) / 2, widthDisplay, heightDisplay);

        }

        shader->createTexture(0, frame->width, frame->height, frame->data[0]);
        shader->createTexture(1, frame->width / 2, frame->height / 2, frame->data[1]);
        shader->createTexture(2, frame->width / 2, frame->height / 2, frame->data[2]);
        shader->draw();
        egl->swapBuffers();


    }
};
