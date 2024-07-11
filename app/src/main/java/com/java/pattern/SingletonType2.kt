package com.java.pattern

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SingletonType2 {
    companion object{
        private var instance: SingletonType2 = SingletonType2()
        fun getInstance(): SingletonType2 {
            return instance
        }
    }
}