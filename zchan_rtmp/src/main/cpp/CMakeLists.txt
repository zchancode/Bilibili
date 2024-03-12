
cmake_minimum_required(VERSION 3.22.1)
project("zchan_rtmp")
file(GLOB src *.cpp)
include_directories(include/ffmpeg)
include_directories(include)
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${src})
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")

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
        opencv_java4
        log)