#include <jni.h>
#include <string>

#include <libfaac/faac.h>
#include "FaacEncoder.cxx"

FaacEncoder *faacEncoder = 0;
FILE *file = 0;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1faacencoder_JniImp_initEncoder(JNIEnv *env, jclass thiz, jint sample_rate,
                                                       jint channels) {
    faacEncoder = new FaacEncoder((u_long) sample_rate, (u_long) channels);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1faacencoder_JniImp_pushPCM(JNIEnv *env, jclass clazz, jbyteArray buffer) {
    jbyte *pcm = env->GetByteArrayElements(buffer, 0);
    int8_t *out = 0;
    int outLength = faacEncoder->encode(pcm, &out);
    free(out);
    env->ReleaseByteArrayElements(buffer, pcm, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1faacencoder_JniImp_stopEncoder(JNIEnv *env, jclass clazz) {
    delete faacEncoder;
    faacEncoder = 0;
}