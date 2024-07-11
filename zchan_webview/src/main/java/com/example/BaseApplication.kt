package com.example

import android.app.Application
import com.example.zchan_webview.loadsir.LoadingCallback
import com.kingja.loadsir.core.LoadSir

open class BaseApplication: Application() {
    companion object{
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}

class App: BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback()) //添加各种状态页
            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()

    }

}