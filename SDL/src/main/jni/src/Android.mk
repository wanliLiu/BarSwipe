LOCAL_PATH := $(call my-dir)

###########################
#
# FFmpeg shared library
#
###########################

FFmpegLib := $(LOCAL_PATH)/../ffmpeg/lib
FFmpegInclude := $(LOCAL_PATH)/../ffmpeg/include

#FFmpeg codec library ----编解码（最重要的库）
include $(CLEAR_VARS)
LOCAL_MODULE:= avcodec
LOCAL_SRC_FILES:= $(FFmpegLib)/libavcodec-57.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg device handling library----各种设备的输入输出
include $(CLEAR_VARS)
LOCAL_MODULE:= avdevice
LOCAL_SRC_FILES:= $(FFmpegLib)/libavdevice-57.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg audio/video filtering library-----滤镜特效处理
include $(CLEAR_VARS)
LOCAL_MODULE:= avfilter
LOCAL_SRC_FILES:= $(FFmpegLib)/libavfilter-6.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg container format library-------封装格式处理
include $(CLEAR_VARS)
LOCAL_MODULE:= avformat
LOCAL_SRC_FILES:= $(FFmpegLib)/libavformat-57.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg utility library-----------工具库（大部分库都需要这个库的支持）
include $(CLEAR_VARS)
LOCAL_MODULE:= avutil
LOCAL_SRC_FILES:= $(FFmpegLib)/libavutil-55.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg postprocessing library----后加工,类似于facebook 后期Bitmap的处理
include $(CLEAR_VARS)
LOCAL_MODULE:= postproc
LOCAL_SRC_FILES:= $(FFmpegLib)/libpostproc-54.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg audio resampling library -------音频采样数据格式转换
include $(CLEAR_VARS)
LOCAL_MODULE:= swresample
LOCAL_SRC_FILES:= $(FFmpegLib)/libswresample-2.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

#FFmpeg image rescaling library---------音频采样数据格式转换
include $(CLEAR_VARS)
LOCAL_MODULE:= swscale
LOCAL_SRC_FILES:= $(FFmpegLib)/libswscale-4.so
LOCAL_EXPORT_C_INCLUDES:= $(FFmpegInclude)
include $(PREBUILT_SHARED_LIBRARY)

###########################
#
# SDL My Custom library
#
###########################

include $(CLEAR_VARS)
LOCAL_MODULE := main

SDL_PATH := ../SDL

#我们项目要用到ffmpeg SDL头文件 但是这里可以不用加入ffmpeg SDL的头文件
#因为编译ffmpeg SDL模块的时候都通过LOCAL_EXPORT_C_INCLUDES加入了各自的头文件路径
#LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDL_PATH)/include \
#                    $(FFmpegInclude)

# Add your application source files here...
LOCAL_SRC_FILES := $(SDL_PATH)/src/main/android/SDL_android_main.c \
    Video_sdl.c \
	main.c

LOCAL_SHARED_LIBRARIES := SDL2 avcodec avdevice avfilter avformat avutil postproc swresample swscale
LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -llog
include $(BUILD_SHARED_LIBRARY)
