#include <jni.h>
#include <android/log.h>
#include <string>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>

jstring
Java_com_example_soli_jnitest_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    __android_log_print(ANDROID_LOG_ERROR, "ProjectName",
                        "native make 通过cmake来做\nnative有两种方式，一是传统的jni方式，另一种是cmake来做，google官方后面可能会用这个作为官方的native开发，jni方式可能会去掉");
    __android_log_print(ANDROID_LOG_ERROR, "ProjectName", "cmake方式要了解CMakeLists.txt的编写，语法等");
    return env->NewStringUTF("Hello from C++,来自底层库函数");
}

jstring
Java_com_example_soli_jnitest_MainActivity_urlprotocolinfo(
        JNIEnv *env, jobject) {
    char info[40000] = {0};
    av_register_all();

    struct URLProtocol *pup = NULL;

    struct URLProtocol **p_temp = &pup;
//    avio_enum_protocols((void **) p_temp, 0);

    while ((*p_temp) != NULL) {
        sprintf(info, "%sInput: %s\n", info, avio_enum_protocols((void **) p_temp, 0));
    }
    pup = NULL;
//    avio_enum_protocols((void **) p_temp, 1);
    while ((*p_temp) != NULL) {
        sprintf(info, "%sInput: %s\n", info, avio_enum_protocols((void **) p_temp, 1));
    }
    return env->NewStringUTF(info);
}

jstring
Java_com_example_soli_jnitest_MainActivity_avformatinfo(
        JNIEnv *env, jobject) {
    char info[40000] = {0};

    av_register_all();

    AVInputFormat *if_temp = av_iformat_next(NULL);
    AVOutputFormat *of_temp = av_oformat_next(NULL);
    while (if_temp != NULL) {
        sprintf(info, "%sInput: %s\n", info, if_temp->name);
        if_temp = if_temp->next;
    }
    while (of_temp != NULL) {
        sprintf(info, "%sOutput: %s\n", info, of_temp->name);
        of_temp = of_temp->next;
    }
    return env->NewStringUTF(info);
}

jstring
Java_com_example_soli_jnitest_MainActivity_avcodecinfo(
        JNIEnv *env, jobject) {
    char info[40000] = {0};

    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while (c_temp != NULL) {
        if (c_temp->decode != NULL) {
            sprintf(info, "%sdecode:", info);
        } else {
            sprintf(info, "%sencode:", info);
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s(video):", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s(audio):", info);
                break;
            default:
                sprintf(info, "%s(other):", info);
                break;
        }
        sprintf(info, "%s[%10s]\n", info, c_temp->name);
        c_temp = c_temp->next;
    }

    return env->NewStringUTF(info);
}

jstring
Java_com_example_soli_jnitest_MainActivity_avfilterinfo(
        JNIEnv *env, jobject) {
    char info[40000] = {0};
    avfilter_register_all();

    AVFilter *f_temp = (AVFilter *) avfilter_next(NULL);
    while (f_temp != NULL) {
        sprintf(info, "%s%s\n", info, f_temp->name);
        f_temp = f_temp->next;
    }
    return env->NewStringUTF(info);
}

}

