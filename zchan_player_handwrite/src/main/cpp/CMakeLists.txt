
cmake_minimum_required(VERSION 3.22.1)
project("zchan_player_handwrite")

include_directories(include)
link_directories(../jniLibs/${ANDROID_ABI})
file(GLOB src *.cxx)
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${src})
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        avcodec
        avfilter
        avformat
        avutil
        fdk-aac
        swresample
        swscale
        x264
        log)