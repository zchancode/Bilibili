
cmake_minimum_required(VERSION 3.22.1)
project("zchan_opengl")
add_library(${CMAKE_PROJECT_NAME} SHARED
        zchan_opengl.cpp
        stb_image.h)
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        EGL
        GLESv3
        log)