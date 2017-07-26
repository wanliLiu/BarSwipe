#include <jni.h>
#include <android/log.h>

extern "C" {
jstring
Java_com_example_soli_jnitest_testJni_getString(
        JNIEnv *env,
        jobject /* this */) {
    __android_log_print(ANDROID_LOG_ERROR, "23", "受到法律上的反抗势力对抗");
    return env->NewStringUTF("Hello from C++,圣诞快乐速度");
}

}

