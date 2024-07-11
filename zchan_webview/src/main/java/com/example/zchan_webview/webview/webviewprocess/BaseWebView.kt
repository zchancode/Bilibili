package com.example.zchan_webview.webview.webviewprocess

import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.zchan_webview.webview.webviewprocess.bean.Action
import com.example.zchan_webview.webview.webviewprocess.client.WebChromeCallBack
import com.example.zchan_webview.webview.webviewprocess.client.WebChromeClient
import com.example.zchan_webview.webview.webviewprocess.client.WebViewCallBack
import com.example.zchan_webview.webview.webviewprocess.client.WebViewClient
import com.example.zchan_webview.webview.webviewprocess.setting.WebViewSetting
import com.google.gson.reflect.TypeToken

/**
Created by Mr.Chan
Time 2024-07-03
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class BaseWebView : android.webkit.WebView {
    constructor(context: android.content.Context) : super(context) {
        init()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        WebViewActionDispatcher.getInstance()?.initAidlConnect()
        val webViewSetting = WebViewSetting.getInstance()
        webViewSetting.setWebSettings(this)
        addJavascriptInterface(this, "zchan")
    }

    fun setWebViewCallBack(callBack: WebViewCallBack) {
        this.setWebViewClient(WebViewClient(callBack))
    }

    fun setWebChromeCallBack(callBack: WebChromeCallBack) {
        this.setWebChromeClient(WebChromeClient(callBack))
    }

    @JavascriptInterface
    fun takeNativeAction(string: String) {
        val action = com.google.gson.Gson().fromJson<Action>(string, object : TypeToken<Action>() {}.type)
        WebViewActionDispatcher.getInstance()?.dispatchAction(action.action, action.params.toString())

    }

}
