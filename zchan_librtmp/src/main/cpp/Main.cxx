#include <jni.h>
#include <string>

#include "X264Encoder.cxx"
#include "FaacEncoder.cxx"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_librtmp",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1librtmp_JniImp_##name

X264Encode *x264Encoder = nullptr;
FaacEncode *faacEncoder = nullptr;

JNI_FUNC(initX264)(JNIEnv *env, jclass clazz, jint width,
                   jint height, jint fps, jint bitrate) {
    x264Encoder = new X264Encode(width, height, fps, bitrate);
}
LibRtmp *libRtmp = nullptr;


JNI_FUNC(initFaac)(JNIEnv *env, jclass clazz, jint sample_rate,
                   jint channels) {
    faacEncoder = new FaacEncode((u_long) sample_rate, (u_long) channels);
}
JNI_FUNC(pushI420)(JNIEnv *env, jclass clazz, jbyteArray a_bytes,
                   jbyteArray b_bytes, jbyteArray c_bytes) {
    jbyte *y = env->GetByteArrayElements(a_bytes, 0);
    jbyte *u = env->GetByteArrayElements(b_bytes, 0);
    jbyte *v = env->GetByteArrayElements(c_bytes, 0);
    x264Encoder->encodeI420(y, u, v);
    env->ReleaseByteArrayElements(a_bytes, y, 0);
    env->ReleaseByteArrayElements(b_bytes, u, 0);
    env->ReleaseByteArrayElements(c_bytes, v, 0);
}

JNI_FUNC(pushNV12)(JNIEnv *env, jclass clazz, jbyteArray a_bytes,
                   jbyteArray b_bytes) {
    jbyte *y = env->GetByteArrayElements(a_bytes, 0);
    jbyte *uv = env->GetByteArrayElements(b_bytes, 0);
    x264Encoder->encodeNV12(y, uv);
    env->ReleaseByteArrayElements(a_bytes, y, 0);
    env->ReleaseByteArrayElements(b_bytes, uv, 0);
}


JNI_FUNC(pushPCM)(JNIEnv *env, jclass clazz, jbyteArray pcm_bytes) {
    jbyte *pcm = env->GetByteArrayElements(pcm_bytes, 0);
    faacEncoder->encode(pcm);
    env->ReleaseByteArrayElements(pcm_bytes, pcm, 0);
}

JNI_FUNC(startRTMP)(JNIEnv *env, jclass clazz) {
    if (libRtmp == nullptr) {
        libRtmp = new LibRtmp("rtmp://139.224.68.119:1935/rtmplive_demo/hls");
    }
    libRtmp->start();
}

JNI_FUNC(release)(JNIEnv *env, jclass clazz) {
    delete libRtmp;
    delete faacEncoder;
    delete x264Encoder;

    libRtmp = nullptr;
    faacEncoder = nullptr;
    x264Encoder = nullptr;

}

