package com.example.playercore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val playerSurface = findViewById<PlayerSurface>(R.id.playerSurface)
        playerSurface.setOnSurfaceCreated {
            PlayInterface.setSurface(it.surface)
            Thread {
                PlayInterface.startPlay("/data/data/com.example.bilibili/test.mp4")
            }.start()
        }
    }


}