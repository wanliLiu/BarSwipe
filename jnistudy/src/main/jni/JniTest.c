#include <jni.h>
#include <android/log.h>

JNIEXPORT jstring JNICALL
Java_com_soli_jnistudy_JniTest_getStringFromJni(JNIEnv *env, jclass type) {

    __android_log_print(ANDROID_LOG_ERROR, "ProjectName", "来自jnistudy的stirng");
    return (*env)->NewStringUTF(env, "this is from library jnistudy string");
}