#include <jni.h>
#include <string>


#include "IPlayerC.cxx"

ANativeWindow *win = 0;
IPlayerC *player = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_playercore_PlayInterface_startPlay(JNIEnv *env, jobject thiz, jstring url) {
    auto url_ = env->GetStringUTFChars(url, 0);
    player = new IPlayerC(url_, win);
    player->startPlay();
    env->ReleaseStringUTFChars(url, url_);
    return 0;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_setSurface(JNIEnv *env, jobject thiz, jobject holder) {
    win = ANativeWindow_fromSurface(env, holder);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_playercore_PlayInterface_stopPlay(JNIEnv *env, jobject thiz) {
    player->stopPlay();
    delete player;
    player = 0;
}