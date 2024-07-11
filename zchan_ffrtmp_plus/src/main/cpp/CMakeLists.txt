
cmake_minimum_required(VERSION 3.22.1)
project("zchan_ffrtmp_plus")
file(GLOB SOURCE_FILES "*.cxx")
include_directories(include)
link_directories(../jniLibs/${ANDROID_ABI})
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${SOURCE_FILES})
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        avcodec
        avfilter
        avformat
        avutil
        swresample
        swscale
        x264
        fdk-aac
        log)