
cmake_minimum_required(VERSION 3.22.1)
project("zchan_x264encoder")
include_directories(include)
file(GLOB SOURCE_FILES
        "*.cxx")
link_directories(${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${SOURCE_FILES})
target_link_libraries(${CMAKE_PROJECT_NAME}
        x264
        android
        log)