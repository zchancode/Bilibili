package com.zchan.login.model

import android.graphics.Bitmap

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ILoginModel {
    fun loadImg(path: String): Bitmap?
    fun login(username: String, password: String, listener: (String) -> Unit)
    fun register(username: String, password: String, listener: (String) -> Unit)
}