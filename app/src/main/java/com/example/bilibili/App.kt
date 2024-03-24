package com.example.bilibili

import android.app.Application
import android.content.Intent
import com.example.zchan_ffrtmp_plus.ui.GameActivity
import com.example.zchan_hardrtmp.ui.HardActivity
import com.example.zchan_opengl.ui.GLActivity
import com.example.zchan_player_plus.ui.PlayerActivity

/**
Created by Mr.Chan
Time 2024-01-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        //intent to PlayerActivity
        val intent = Intent(this, HardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}