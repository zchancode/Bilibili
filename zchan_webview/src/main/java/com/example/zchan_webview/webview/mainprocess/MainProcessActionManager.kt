package com.example.zchan_webview.webview.mainprocess

import android.util.Log
import android.widget.Toast
import com.example.BaseApplication
import com.example.common.autoservice.IWebViewService
import com.example.zchan_webview.IWebViewProcessToMainProcessAidlInterface
import kotlin.math.log

/**
Created by Mr.Chan
Time 2024-07-04
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class MainProcessActionManager : IWebViewProcessToMainProcessAidlInterface.Stub() {
    companion object {
        private var sInstance: MainProcessActionManager? = null
        fun getInstance(): MainProcessActionManager? {
            if (sInstance == null) {
                synchronized(MainProcessActionManager::class.java) {
                    if (sInstance == null) {
                        sInstance = MainProcessActionManager()
                    }
                }
            }
            return sInstance
        }
    }

    fun runAction(action: String?, message: String?) {
        //message to map
        val params = com.google.gson.Gson().fromJson(message, java.util.HashMap::class.java)
        when (action) {
            "showToast" -> {
                val content = params["message"] as String
                Log.e("TAG", "runAction: " + content)
            }

            "openActivity" -> {
                val targetClass = params["target"] as String
                val intent = android.content.Intent()
                intent.setClassName(BaseApplication.instance, targetClass)
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                BaseApplication.instance.startActivity(intent)
            }
        }

    }

    override fun handleWebAction(action: String?, message: String?) {
        getInstance()?.runAction(action, message)
    }


}