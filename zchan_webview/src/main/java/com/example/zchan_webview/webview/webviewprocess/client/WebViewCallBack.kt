package com.example.zchan_webview.webview.webviewprocess.client

/**
Created by Mr.Chan
Time 2024-07-03
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface WebViewCallBack {
    fun pageStarted(url: String?)
    fun pageFinished(url: String?)
    fun onError(errorCode: Int, description: String, failingUrl: String)

}