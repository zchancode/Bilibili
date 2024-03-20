
cmake_minimum_required(VERSION 3.22.1)
project("zchan_faacencoder")
file(GLOB SOURCE *.cxx)
link_directories(../jniLibs/${ANDROID_ABI})
include_directories(include)
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${SOURCE})
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        faac
        log)