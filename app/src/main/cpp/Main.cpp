#include <jni.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <cstring>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "FFMPEG", __VA_ARGS__)

