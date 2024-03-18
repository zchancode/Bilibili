
cmake_minimum_required(VERSION 3.6.0)
#c++ 17
set(CMAKE_CXX_STANDARD 17)
file(GLOB SOURCE_FILES
        *.cxx
        decode/*.cxx
        resample/*.cxx
        video/*.cxx
        audio/*.cxx)
include_directories(decode)
include_directories(resample)
include_directories(video)
include_directories(audio)
include_directories(./)

project("zchan_structure")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")
include_directories(include)

add_library(${CMAKE_PROJECT_NAME} SHARED
        ${SOURCE_FILES})

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        GLESv3 EGL
        OpenSLES
        ffmpeg
        log)