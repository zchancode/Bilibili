# For more information about using CMake with Android Studio, read the

cmake_minimum_required(VERSION 3.22.1)

project("playercore")
include_directories(include)
file(GLOB src *.cxx)
file(GLOB decode decode/*.cxx)
file(GLOB video video/*.cxx)
file(GLOB audio audio/*.cxx)
file(GLOB resample resample/*.cxx)


find_library(log-lib log)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")
add_library(${CMAKE_PROJECT_NAME} SHARED
        ${src}
        ${decode}
        ${video}
        ${audio}
        ${resample})

target_link_libraries(${CMAKE_PROJECT_NAME}
        GLESv3 EGL
        OpenSLES
        ffmpeg
        android
        log)