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

using namespace std;

#include <IPlayer.cxx>
IPlayer *player = 0;
extern "C" {
#include <libavformat/avformat.h>
}

ANativeWindow *win = 0;
JNI_FUNC(setSurface)(JNIEnv *env, jclass clazz, jobject surface) {
    win = ANativeWindow_fromSurface(env, surface);
}

JNI_FUNC(startPlay)(JNIEnv *env, jclass clazz, jstring url_) {
    const char *urlStr = env->GetStringUTFChars(url_, 0);
    string url = urlStr;
    player = new IPlayer(url, win);
    player->startPlay();
    env->ReleaseStringUTFChars(url_, urlStr);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1structure_JniImp_stopPlay(JNIEnv *env, jclass clazz) {
    player->stopPlay();
}