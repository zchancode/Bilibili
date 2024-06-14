#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>
#include "MainInterface.cxx"
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_player_annotating",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1player_1annotating_JniImp_##name

ANativeWindow *win = 0;
MainInterface *mainInterface = 0;
JNI_FUNC(startPlay)
(JNIEnv *env, jclass thiz,jstring url) {
    const char *urlStr = env->GetStringUTFChars(url, 0);
    mainInterface = new MainInterface(urlStr, win);
    mainInterface->startPlay();
    env->ReleaseStringUTFChars(url, urlStr);
}

JNI_FUNC(setSurface)
(JNIEnv *env, jclass clazz, jobject surface) {
    win = ANativeWindow_fromSurface(env, surface);
}
JNI_FUNC(stopPlay)(JNIEnv *env, jclass clazz) {
    mainInterface->stopPlay();
}