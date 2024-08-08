package com.example.playercore

object PlayInterface {

    init {
        System.loadLibrary("playercore")
    }

    external fun startPlay(url: String): Int
    external fun stopPlay()
    external fun setSurface(holder: Any)

    external fun replay(url: String)

    external fun seekTo(time: Int)

}