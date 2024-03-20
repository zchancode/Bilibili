#include <jni.h>
#include "X264Encoder.cxx"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_x264encoder",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1x264encoder_JniImp_##name

X264Encoder *encoder = nullptr;
JNI_FUNC(initX264)(JNIEnv *env, jclass clazz, jint width,
                   jint height, jint fps, jint bitrate) {
    encoder = new X264Encoder(width, height, fps, bitrate, "/sdcard/Download/zchan7.h264");

}
JNI_FUNC(pushI420)(JNIEnv *env, jclass clazz, jbyteArray a_bytes,
                    jbyteArray b_bytes, jbyteArray c_bytes) {
    jbyte *y = env->GetByteArrayElements(a_bytes, 0);
    jbyte *u = env->GetByteArrayElements(b_bytes, 0);
    jbyte *v = env->GetByteArrayElements(c_bytes, 0);
    encoder->encodeI420(y, u, v);
    env->ReleaseByteArrayElements(a_bytes, y, 0);
    env->ReleaseByteArrayElements(b_bytes, u, 0);
    env->ReleaseByteArrayElements(c_bytes, v, 0);
}

JNI_FUNC(pushNV12)(JNIEnv *env, jclass clazz, jbyteArray a_bytes,
                   jbyteArray b_bytes) {
    jbyte *y = env->GetByteArrayElements(a_bytes, 0);
    jbyte *uv = env->GetByteArrayElements(b_bytes, 0);
    encoder->encodeNV12(y, uv);
    env->ReleaseByteArrayElements(a_bytes, y, 0);
    env->ReleaseByteArrayElements(b_bytes, uv, 0);
}

JNI_FUNC(releaseX264)(JNIEnv *env, jclass clazz) {
    delete encoder;
    encoder = nullptr;
}