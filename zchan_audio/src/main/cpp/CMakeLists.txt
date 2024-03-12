
cmake_minimum_required(VERSION 3.22.1)
project("zchan_audio")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")
file(GLOB srcs *.cpp)
include_directories(include)
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${srcs})
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        ffmpeg
        OpenSLES
        log)