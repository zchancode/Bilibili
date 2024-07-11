
cmake_minimum_required(VERSION 3.22.1)
project("zchan_hardrtmp")

file(GLOB librtmp librtmp/*.c)
include_directories(librtmp)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

add_library(${CMAKE_PROJECT_NAME} SHARED
        ${librtmp}
        MainHard.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)