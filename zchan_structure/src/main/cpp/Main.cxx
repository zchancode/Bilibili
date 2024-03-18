#include <jni.h>
#include <android/native_window_jni.h>
#include <GLES3/gl3.h>
#include "EGL.cxx"
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1structure_JniImp_##name
#include "Demux.cxx"
#include <android/log.h>
#define TAG "zchan_structure"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#include <string>
#include "AudioDecode.cxx"
#include "VideoDecode.cxx"
using namespace std;

extern "C" {
#include <libavformat/avformat.h>
}


JNI_FUNC(setSurface)(JNIEnv *env, jclass clazz, jobject surface) {
    EGL egl(ANativeWindow_fromSurface(env, surface));
    egl.init();
    glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    egl.swapBuffers();
}

JNI_FUNC(startPlay)(JNIEnv *env, jclass clazz, jstring url_) {
    const char *urlStr = env->GetStringUTFChars(url_, 0);
    Demux* demux = new Demux(urlStr);
    LOGE("totalMs: %lld", demux->getTotalMs());
    AudioDecode* audioDecode = new AudioDecode(demux->getAvFormatContext());
    VideoDecode* videoDecode = new VideoDecode(demux->getAvFormatContext());
    demux->addObserver(audioDecode);
    demux->addObserver(videoDecode);
    demux->start();
    videoDecode->start();
    audioDecode->start();


    env->ReleaseStringUTFChars(url_, urlStr);
}