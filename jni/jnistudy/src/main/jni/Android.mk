LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := JniTest
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \
	-lz \
	-lm \
	-ljnigraphics \

LOCAL_SRC_FILES := \
	E:\androidWorkplace\BarSwipe\jni\jnistudy\src\main\jni\JniTest.c \

LOCAL_C_INCLUDES += E:\androidWorkplace\BarSwipe\jni\jnistudy\src\main\jni
LOCAL_C_INCLUDES += E:\androidWorkplace\BarSwipe\jni\jnistudy\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
