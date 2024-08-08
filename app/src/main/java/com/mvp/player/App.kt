package com.mvp.player

import android.app.Application
import android.util.Log

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class App : Application() {

    companion object {
        lateinit var instance: App
    }

    fun getToken(): String {
        val sharePreference = getSharedPreferences("user", MODE_PRIVATE)
        return sharePreference.getString("token", "").toString()
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}