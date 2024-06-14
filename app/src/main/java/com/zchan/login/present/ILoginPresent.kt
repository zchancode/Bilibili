package com.zchan.login.present

import android.graphics.Bitmap

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ILoginPresent {
    fun loadImg(path: String): Bitmap?
    fun login(username: String, password: String)
    fun register(username: String, password: String)
}