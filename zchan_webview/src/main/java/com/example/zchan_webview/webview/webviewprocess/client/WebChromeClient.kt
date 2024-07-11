package com.example.zchan_webview.webview.webviewprocess.client

import android.webkit.WebView

/**
Created by Mr.Chan
Time 2024-07-03
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class WebChromeClient: android.webkit.WebChromeClient {
    private var callBack: WebChromeCallBack? = null
    constructor(callBack: WebChromeCallBack) : super() {
        this.callBack = callBack
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        callBack?.onReceivedTitle(title)
    }

    override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
        return super.onConsoleMessage(consoleMessage)
    }
}