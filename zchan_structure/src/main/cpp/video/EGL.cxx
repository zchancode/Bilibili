//
// Created by Administrator on 2024-03-17.
//
#pragma once

#include <EGL/egl.h>
class EGL {
private:
    EGLSurface surface = EGL_NO_SURFACE;
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLContext context = EGL_NO_CONTEXT;
    ANativeWindow *win = 0;
public:
    EGL(ANativeWindow *win){
        this->win = win;
    }

    ~EGL(){
        eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        eglDestroySurface(display, surface);
        eglDestroyContext(display, context);
        eglTerminate(display);
    }
    void init(){
        display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(display, 0, 0);
        EGLConfig config;
        EGLint numConfigs;
        const EGLint attribs[] = {
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                EGL_NONE
        };
        eglChooseConfig(display, attribs, &config, 1, &numConfigs);
        surface = eglCreateWindowSurface(display, config, win, NULL);
        const EGLint ctx_attribs[] = {
                EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL_NONE
        };
        EGLContext context = eglCreateContext(display, config, EGL_NO_CONTEXT, ctx_attribs);
        eglMakeCurrent(display, surface, surface, context);
    }

    void swapBuffers(){
        eglSwapBuffers(display, surface);
    }

};