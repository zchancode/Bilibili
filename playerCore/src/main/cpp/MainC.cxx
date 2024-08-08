#pragma once
#include <jni.h>
#include <string>


#include "LogC.cxx"
#include "IPlayerC.cxx"

ANativeWindow *win = 0;
IPlayerC *player = 0;
LogC *mlog = 0;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    mlog = new LogC(vm);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    mlog->releaseGLogClass();
    delete mlog;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_playercore_PlayInterface_startPlay(JNIEnv *env, jobject thiz, jstring url) {
    auto url_ = env->GetStringUTFChars(url, 0);
    player = new IPlayerC(url_, win, mlog);
    player->startPlay();
    env->ReleaseStringUTFChars(url, url_);
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_setSurface(JNIEnv *env, jobject thiz, jobject holder) {
    win = ANativeWindow_fromSurface(env, holder);
    mlog->log("setSurface", env);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_stopPlay(JNIEnv *env, jobject thiz) {
    if (player) {
        player->stopPlay();
        delete player;
        player = 0;
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_replay(JNIEnv *env, jobject thiz, jstring url) {
    if (player) {
        player->stopPlay();
        delete player;
        player = 0;
    }
    auto url_ = env->GetStringUTFChars(url, 0);
    player = new IPlayerC(url_, win, mlog);
    player->startPlay();
    env->ReleaseStringUTFChars(url, url_);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_seekTo(JNIEnv *env, jobject thiz, jint time) {
    if (player) {
        player->seekTo(time);
    }
}