//
// Created by Administrator on 2024-03-26.
//
#pragma once

#include <EGL/egl.h>
#include <android/native_window.h>

class AndroidEGL {
private:
    EGLSurface surface = EGL_NO_SURFACE;
    EGLDisplay display = EGL_NO_DISPLAY;
    EGLContext context = EGL_NO_CONTEXT;
    ANativeWindow *win = nullptr;
public:
    AndroidEGL(ANativeWindow *win) {
        this->win = win;
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
        context = eglCreateContext(display, config, EGL_NO_CONTEXT, ctx_attribs);
        eglMakeCurrent(display, surface, surface, context);

    }

    ~AndroidEGL() {
        eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        eglDestroySurface(display, surface);
        eglDestroyContext(display, context);
        eglTerminate(display);
        ANativeWindow_release(win);
    }


    void swapBuffers() {
        eglSwapBuffers(display, surface);
    }

};