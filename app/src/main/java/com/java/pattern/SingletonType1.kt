package com.java.pattern

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

object SingletonType1 {
//饿汉式
    private var instance: SingletonType1? = null
    fun getInstance(): SingletonType1 {
        return instance ?: SingletonType1
    }

}