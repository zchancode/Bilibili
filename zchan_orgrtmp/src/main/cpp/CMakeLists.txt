cmake_minimum_required(VERSION 3.22.1)
project("zchan_orgrtmp")
file(GLOB srcs *.cxx)
file(GLOB librtmp librtmp/*.c)
include_directories(librtmp)
include_directories(include)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
link_directories(${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${librtmp}
        ${srcs}
        )

target_link_libraries(
        ${CMAKE_PROJECT_NAME}
        x264
        faac
        swresample
        swscale
        avutil
        android
        log
)