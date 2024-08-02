package com.mvp.player

import android.app.Application
import android.util.Log

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class App : Application() {

    var token: String? = null

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        val sharePreference = getSharedPreferences("user", MODE_PRIVATE)
        token = sharePreference.getString("token", null)

        Log.d("App", "token $token")
    }
}