#include <android/log.h>
#include <jni.h>
#include <string>

#define TAG "Demo-Native"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define CRASH 0

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_MainActivity_getNativeHello(JNIEnv *env, jclass object) {
    std::string hello = "native-hello";
    LOG("getNativeHello");
#if CRASH
    int *p = NULL;
    *p = 100;    // 空指针写入 → SIGSEGV 段错误，native 崩溃
#endif
    return env->NewStringUTF(hello.c_str());
}
