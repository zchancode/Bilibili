#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_stringFromJNI(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF("Hello from C++");
}