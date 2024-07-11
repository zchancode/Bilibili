package com.example.zchan_webview.webview.mainprocess

import android.content.Intent
import android.os.IBinder

/**
Created by Mr.Chan
Time 2024-07-04
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class MainProcessService : android.app.Service() {
    override fun onBind(intent: Intent?): IBinder? { //用aidl的方式进行进程间通信
        return MainProcessActionManager.getInstance()
    }
}