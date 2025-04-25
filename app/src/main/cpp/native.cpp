#include <android/log.h>
#include <jni.h>
#include <string>

#define TAG "Demo-Native"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_ji_demo_MainActivity_getNativeHello(JNIEnv *env, jclass object) {
    std::string hello = "native-hello";
    LOG("getNativeHello");
    return env->NewStringUTF(hello.c_str());
}
