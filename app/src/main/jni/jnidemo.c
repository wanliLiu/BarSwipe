#include <jni.h>
#include "com_barswipe_jni_Jnidemo.h"
#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "ProjectName", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , "ProjectName", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , "ProjectName", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN  , "ProjectName", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "ProjectName", __VA_ARGS__)

JNIEXPORT jstring JNICALL
Java_com_barswipe_jni_Jnidemo_getStringFromJni(JNIEnv *env, jobject type) {

    LOGE("我在jni中打印的日志");
    return (*env)->NewStringUTF(env,"This is the string from JNI");
}