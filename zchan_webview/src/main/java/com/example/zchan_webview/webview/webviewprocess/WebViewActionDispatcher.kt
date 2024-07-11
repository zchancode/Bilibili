package com.example.zchan_webview.webview.webviewprocess

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.BaseApplication
import com.example.zchan_webview.IWebViewProcessToMainProcessAidlInterface
import com.example.zchan_webview.webview.mainprocess.MainProcessService

/**
Created by Mr.Chan
Time 2024-07-04
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class WebViewActionDispatcher : ServiceConnection {
    //connect to MainProcessService
    private var iWebViewProcessToMainProcessAidlInterface: IWebViewProcessToMainProcessAidlInterface? = null

    companion object {
        private var instance: WebViewActionDispatcher? = null
        fun getInstance(): WebViewActionDispatcher? {
            if (instance == null) {
                synchronized(WebViewActionDispatcher::class.java) {
                    if (instance == null) {
                        instance = WebViewActionDispatcher()
                    }
                }
            }
            return instance
        }
    }


    fun initAidlConnect() {
        val intent = Intent(BaseApplication.instance, MainProcessService::class.java)
        BaseApplication.instance.bindService(intent, this, android.content.Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iWebViewProcessToMainProcessAidlInterface =
            IWebViewProcessToMainProcessAidlInterface.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        iWebViewProcessToMainProcessAidlInterface = null
        initAidlConnect()
    }

    fun dispatchAction(actionName: String?, params: String?) {
        iWebViewProcessToMainProcessAidlInterface?.handleWebAction(actionName, params)
    }
}