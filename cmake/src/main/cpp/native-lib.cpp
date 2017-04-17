#include <jni.h>
#include <android/log.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_soli_jnitest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    __android_log_print(ANDROID_LOG_ERROR, "ProjectName", "native make 通过cmake来做\nnative有两种方式，一是传统的jni方式，另一种是cmake来做，google官方后面可能会用这个作为官方的native开发，jni方式可能会去掉");
    __android_log_print(ANDROID_LOG_ERROR, "ProjectName", "cmake方式要了解CMakeLists.txt的编写，语法等");
    return env->NewStringUTF("Hello from C++");
}
