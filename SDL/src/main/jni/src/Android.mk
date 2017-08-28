LOCAL_PATH := $(call my-dir)


###########################
#
# FFmpeg shared library
#
###########################

include $(CLEAR_VARS)

LOCAL_MODULE:= avcodec

LOCAL_SRC_FILES:= lib/libavcodec-57.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE:= avformat

LOCAL_SRC_FILES:= lib/libavformat-57.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)



include $(CLEAR_VARS)

LOCAL_MODULE:= swscale

LOCAL_SRC_FILES:= lib/libswscale-4.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE:= avutil

LOCAL_SRC_FILES:= lib/libavutil-55.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE:= avfilter

LOCAL_SRC_FILES:= lib/libavfilter-6.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE:= swresample

LOCAL_SRC_FILES:= lib/libswresample-2.so

LOCAL_EXPORT_C_INCLUDES:= $(LOCAL_PATH)/include

include $(PREBUILT_SHARED_LIBRARY)















include $(CLEAR_VARS)

LOCAL_MODULE := main

SDL_PATH := ../SDL

LOCAL_C_INCLUDES := $(LOCAL_PATH)/$(SDL_PATH)/include

# Add your application source files here...
LOCAL_SRC_FILES := $(SDL_PATH)/src/main/android/SDL_android_main.c \
    Video_sdl.c \
	main.c

LOCAL_SHARED_LIBRARIES := SDL2

LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -llog

include $(BUILD_SHARED_LIBRARY)
