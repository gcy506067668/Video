LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

GSTREAMER_SDK_ROOT_ANDROID := D:\Android\smartHome\gstreamerHome
GSTREAMER_ROOT := D:\Android\smartHome\gstreamerHome

LOCAL_MODULE    := gstreamer3
LOCAL_SRC_FILES := gstreamer3.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)


LOCAL_MODULE    := sendsound
LOCAL_SRC_FILES := sendsound.c
LOCAL_SHARED_LIBRARIES := gstreamer_android
LOCAL_LDLIBS := -llog -landroid

include $(BUILD_SHARED_LIBRARY)

ifndef GSTREAMER_ROOT
ifndef GSTREAMER_ROOT_ANDROID
$(error GSTREAMER_ROOT_ANDROID is not defined!)
endif
GSTREAMER_ROOT        := $(GSTREAMER_ROOT_ANDROID)
endif
GSTREAMER_NDK_BUILD_PATH  := $(GSTREAMER_ROOT)/share/gst-android/ndk-build/
include $(GSTREAMER_NDK_BUILD_PATH)/plugins.mk
GSTREAMER_PLUGINS         := $(GSTREAMER_PLUGINS_CORE) $(GSTREAMER_PLUGINS_SYS) $(GSTREAMER_PLUGINS_EFFECTS) $(GSTREAMER_PLUGINS_NET) $(GSTREAMER_PLUGINS_CODECS_RESTRICTED) $(GSTREAMER_PLUGINS_CODECS) opengl $(GSTREAMER_PLUGINS_PLAYBACK)
GSTREAMER_EXTRA_DEPS      := gstreamer-video-1.0
include $(GSTREAMER_NDK_BUILD_PATH)/gstreamer-1.0.mk