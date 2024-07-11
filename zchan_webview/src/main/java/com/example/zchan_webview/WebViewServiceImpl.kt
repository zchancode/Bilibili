package com.example.zchan_webview

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.common.autoservice.IWebViewService
import com.example.zchan_webview.webview.webviewprocess.WebActivity
import com.google.auto.service.AutoService


/**
Created by Mr.Chan
Time 2024-07-01
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

@AutoService(IWebViewService::class)
class WebViewServiceImpl : IWebViewService {
    override fun startWebActivity(
        context: Context?,
        url: String,
        title: String,
        hasTitle: Boolean
    ) {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("url", url)
        intent.putExtra("hasTitle", hasTitle)
        context?.startActivity(intent)
    }

    override fun getWebViewFragment(url: String): Fragment {
        return WebFragment.newInstance(url)
    }

    override fun loadLocalPage(context: Context?, url: String, title: String, hasTitle: Boolean) {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra("title", title)
        intent.putExtra("url", url)
        intent.putExtra("hasTitle", hasTitle)
        context?.startActivity(intent)
    }

}