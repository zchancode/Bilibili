cmake_minimum_required(VERSION 3.22.1)
project("zchan_player")
include_directories(include)
include_directories(video)
file(GLOB src *.cpp)
file(GLOB video_src video/*.cpp)


find_library(log-lib log)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${src}
        ${video_src})
target_link_libraries(${CMAKE_PROJECT_NAME}
        GLESv3 EGL
        OpenSLES
        ffmpeg
        android
        log)