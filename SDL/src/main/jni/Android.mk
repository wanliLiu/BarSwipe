LOCAL_PATH := $(call my-dir)
include $(call all-subdir-makefiles)


#可以通过查询 《Android.mk语法》来了解更多和Android.mk相关的知识
#Android.mk 文件语法详解
#http://www.cnblogs.com/wainiwann/p/3837936.html
##############################----Android.mk 说明-----###################################
#
# LOCAL_PATH := $(call my-dir)
# include $(CLEAR_VARS)
# LOCAL_MODULE := helloworld
# LOCAL_SRC_FILES := helloworld.c
# include $(BUILD_SHARED_LIBRARY)
#
# LOCAL_PATH := $(call my-dir) ，一个Android.mk file首先必须定义好LOCAL_PATH变量。它用于在开发树中查找源文件。在这个例子中，宏函数‘my-dir’, 由编译系统提供，用于返回当前路径（即包含Android.mk file文件的目录）
#
# include $( CLEAR_VARS)，CLEAR_VARS由编译系统提供（(可以在 android 安装目录下的/build/core/config.mk 文件看到其定义，为 CLEAR_VARS:= $(BUILD_SYSTEM)/clear_vars.mk)），指定让GNU MAKEFILE为你清除许多LOCAL_XXX变量（例如 LOCAL_MODULE, LOCAL_SRC_FILES, LOCAL_STATIC_LIBRARIES, 等等...),除LOCAL_PATH 。这是必要的，因为所有的编译控制文件都在同一个GNU MAKE执行环境中，所有的变量都是全局的
#
# LOCAL_MODULE := helloworld，LOCAL_MODULE变量必须定义，以标识你在Android.mk文件中描述的每个模块。名称必须是唯一的。注意编译系统会自动产生合适的前缀和后缀，换句话说，一个被命名为'foo'的共享库模块，将会生成'libfoo.so'文件（也可以直接已libxxx命名好）
#
# LOCAL_SRC_FILES := helloworld.c，LOCAL_SRC_FILES变量必须包含将要编译打包进模块中的C或C++源代码文件。注意，你不用在这里列出头文件和包含文件，因为编译系统将会自动为你找出依赖型的文件；仅仅列出直接传递给编译器的源代码文件就好
#
# LOCAL_C_INCLUDES：可选变量，表示头文件的搜索路径。默认的头文件的搜索路径是LOCAL_PATH目录。示例：LOCAL_C_INCLUDES := sources/foo或LOCAL_C_INCLUDES := $(LOCAL_PATH)/../foo
#
# TARGET_ARCH：目标 CPU平台的名字
# TARGET_PLATFORM：Android.mk 解析的时候，目标 Android 平台的名字
# TARGET_ARCH_ABI：暂时只支持两个 value，armeabi 和 armeabi-v7a
#
# LOCAL_STATIC_LIBRARIES: 表示该模块需要使用哪些静态库，以便在编译时进行链接
# LOCAL_SHARED_LIBRARIES:  表示模块在运行时要依赖的共享库（动态库），在链接时就需要，以便在生成文件时嵌入其相应的信息
#
# PREBUILT_SHARED_LIBRARY: 把这个共享库声明为 “一个” 独立的模块,此时模块的LOCAL_SRC_FILES应该被指定为一个预先编译好的动态库，而非source file
# PREBUILT_STATIC_LIBRARY:  主要是用在将已经编译好的第三方库
# BUILD_SHARED_LIBRARY:
# BUILD_STATIC_LIBRARY:
#
# LOCAL_EXPORT_C_INCLUDES :一个第三方的依赖库头文件需要被自己写的so所所用，那么在编译这个第三方库的时候应该用此标记这个include需要在别的地方使用
# LOCAL_C_INCLUDES ：自己写so的头文件位置
#
# LOCAL_LDLIBS:  编译模块时要使用的附加的链接器选项
#
# LOCAL_CFLAGS:  可选的编译器选项，在编译 C 代码文件的时候使用，相当于#define isEnable 2
# androdi.mk中定义
#  ifeq ($(BACKCAR_DATA_SOURCE), GPIO)
#     CFLAGS += -DGPIO_ENABLE
#  endif
# C语言中使用：
# ifdef GPIO_ENABLE
#     *prStatu = BCWaitProtocolFromGPIO();
# endif
#
# include $(call all-subdir-makefiles)：返回一个位于当前'my-dir'路径的子目录中的所有Android.mk的列表
#
#
#
# 实现遍历一个目录下的所有文件（但是不会递归调用）-----wildcard
# LOCAL_SRC_FILES := $(wildcard $(LOCAL_PATH)/../*.c)
# 通过wildcard可以进行文件遍历，如果是单目录结构，通过这个同样可以达到非常简洁的效果。如果是c++代码的话（*.cpp文件），需要使用下面的方式，否则可能找不到文件
# FILE_LIST := $(wildcard $(LOCAL_PATH)/../*.cpp)
# LOCAL_SRC_FILES := $(FILE_LIST:$(LOCAL_PATH)/%=%)
#
#


