//
// Created by Administrator on 2024-08-07.
//
#pragma once

#include <string>
#include <jni.h>

class LogC {
public:
    JavaVM *gJvm = NULL;
    jclass gLogClass = NULL;

    LogC(JavaVM *jvm) {
        gJvm = jvm;
        JNIEnv* env = nullptr;
        jvm->GetEnv((void**)&env, JNI_VERSION_1_6);
        jclass localLogClass = env->FindClass("com/example/playercore/Log");
        gLogClass = reinterpret_cast<jclass>(env->NewGlobalRef(localLogClass));
        env->DeleteLocalRef(localLogClass);
    }


    JNIEnv *getEnv() {
        JNIEnv *env = NULL;
        if (gJvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
            if (gJvm->AttachCurrentThread(&env, NULL) != 0) {
                return NULL;
            }
        }
        return env;
    }

    void releaseGLogClass() {
        JNIEnv *env = getEnv();
        env->DeleteGlobalRef(gLogClass);
    }


    void releaseEnv() {
        gJvm->DetachCurrentThread();
    }

    //call com.example.playercore.PlayerActivity  onMessage(String msg)
    void log(std::string msg, JNIEnv *env) {
        jmethodID mid = env->GetStaticMethodID(gLogClass, "onMessage", "(Ljava/lang/String;)V");
        jstring jmsg = env->NewStringUTF(msg.c_str());
        (env)->CallStaticVoidMethod(gLogClass, mid, jmsg);
        env->DeleteLocalRef(jmsg);
    }
};