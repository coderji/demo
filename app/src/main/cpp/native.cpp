#include <android/log.h>
#include <jni.h>
#include "unlock.h"

#define TAG "Demo-Native"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_MainActivity_getUnlockCode(JNIEnv *env, jclass clazz, jstring sn) {
    const char *snBuff = env->GetStringUTFChars(sn, nullptr);
    const jsize snLength = env->GetStringUTFLength(sn);
    char code[33] = {0};
    getUnlockCode((char *) snBuff, snLength, code);
    env->ReleaseStringUTFChars(sn, snBuff);
    return env->NewStringUTF(code);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_MainActivity_getCrash(JNIEnv *env, jclass clazz) {
    char s = 's';
    char c = *(&s - 0xFFFFFFFF);
    if (c) {
        LOG("c:%c", c);
    }
    return env->NewStringUTF("getCrash");
}
