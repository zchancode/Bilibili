package com.java.pattern

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SingletonType2 private constructor() {
    companion object{
        private var instance: SingletonType2? = null
        fun getInstance(): SingletonType2 {
            return instance?: SingletonType2()
        }
    }
}