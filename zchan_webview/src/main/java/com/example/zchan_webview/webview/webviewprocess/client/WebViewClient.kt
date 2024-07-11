package com.example.zchan_webview.webview.webviewprocess.client

import android.graphics.Bitmap
import android.webkit.WebView

/**
Created by Mr.Chan
Time 2024-07-03
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class WebViewClient : android.webkit.WebViewClient {
    private var callBack: WebViewCallBack? = null
    constructor(callBack: WebViewCallBack) : super() {
        this.callBack = callBack

    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        callBack?.pageStarted(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        callBack?.pageFinished(url)
    }

    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        callBack?.onError(errorCode, description.toString(), failingUrl.toString())
    }
}