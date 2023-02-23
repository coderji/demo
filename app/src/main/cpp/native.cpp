#include <android/log.h>
#include <jni.h>
#include "unlock.h"

#define TAG "Demo"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_CaseFragment_getUnlockCode(JNIEnv *env, jclass clazz, jstring sn) {
    const jchar *snBuff = env->GetStringChars(sn, nullptr);
    const jsize snLength = env->GetStringLength(sn);
    char code[CODE_LENGTH + 1];
    getUnlockCode((char *) snBuff, snLength, code);
    env->ReleaseStringChars(sn, snBuff);
    return env->NewStringUTF(code);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_CaseFragment_getCrash(JNIEnv *env, jclass clazz) {
    char s = 's';
    char c = *(&s - 0xFFFFFFFF);
    if (c) {
        LOG("c:%c", c);
    }
    return env->NewStringUTF("getCrash");
}
