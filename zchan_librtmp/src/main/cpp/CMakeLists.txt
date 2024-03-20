
cmake_minimum_required(VERSION 3.22.1)
project("zchan_librtmp")
include_directories(include)
link_directories(../jniLibs/${ANDROID_ABI})
file(GLOB librtmp librtmp/*.c)
include_directories(librtmp)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${librtmp}
        Main.cxx
        LibRtmp.cxx
        SafeQueue.cxx
        X264Encoder.cxx
        FaacEncoder.cxx)
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        x264
        faac
        avutil
        log)