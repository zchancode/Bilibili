cmake_minimum_required(VERSION 3.18.1)
project(bilibili)

file(GLOB src *.cpp)

add_library(bilibili SHARED
       ${src})
include_directories(include)

find_library(log-lib log)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_CURRENT_LIST_DIR}/../jniLibs/${ANDROID_ABI}")

target_link_libraries(
        bilibili
        android
        ${log-lib})