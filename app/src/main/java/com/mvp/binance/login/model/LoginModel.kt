package com.mvp.binance.login.model

import android.graphics.Bitmap

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class LoginModel: ILoginModel {
    override fun loadImg(path: String) : Bitmap? {
        //black bitmap
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        for (i in 0 until 100){
            for (j in 0 until 100){
                bitmap.setPixel(i, j, 0xff000000.toInt())
            }
        }
        return bitmap
    }

    override fun login(username: String, password: String, listener: (String) -> Unit) {
        Thread(Runnable {
            Thread.sleep(2000)
            if (username == "zchan" && password == "123456"){
                listener("登录成功")
            }else{
                listener("登录失败")
            }
        }).start()
    }

    override fun register(username: String, password: String, listener: (String) -> Unit) {
        if (username == "zchan" && password == "123456"){
            listener("注册成功")
        }else{
            listener("注册失败")
        }
    }

}