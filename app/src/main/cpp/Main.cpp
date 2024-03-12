#include <jni.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>
#include <cstring>

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "FFMPEG", __VA_ARGS__)



extern "C"
JNIEXPORT void JNICALL
Java_com_example_bilibili_view_TGLSurfaceView2_Open(JNIEnv *env, jobject thiz, jstring url,
                                                    jobject surface) {

}