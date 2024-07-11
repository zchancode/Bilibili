package com.example.zchan_webview.webview.webviewprocess.setting

import android.webkit.WebView

/**
Created by Mr.Chan
Time 2024-07-03
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class WebViewSetting {
    private var mWebSettings: android.webkit.WebSettings? = null

    private constructor()

    companion object {
        private val instance: WebViewSetting = WebViewSetting()
        fun getInstance(): WebViewSetting {
            return instance
        }
    }

    fun setWebSettings(webView: WebView) {
        mWebSettings = webView.settings
        mWebSettings?.javaScriptEnabled = true
    }

}