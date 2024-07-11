package com.example.zchan_webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.zchan_webview.webview.webviewprocess.client.WebChromeCallBack
import com.example.zchan_webview.webview.webviewprocess.client.WebViewCallBack
import com.example.zchan_webview.databinding.FragmentWebBinding
import com.example.zchan_webview.loadsir.LoadingCallback
import com.example.zchan_webview.webview.webviewprocess.WebActivity
import com.example.zchan_webview.webview.webviewprocess.setting.WebViewSetting
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir


/**
Created by Mr.Chan
Time 2024-07-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class WebFragment private constructor() : Fragment(), WebViewCallBack, WebChromeCallBack {
    private var mUrl: String? = null
    private var mLoadService : LoadService<*>? = null
    private lateinit var view: FragmentWebBinding
    companion object {
        fun newInstance(url: String): WebFragment {
            val fragment = WebFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        mUrl = bundle?.getString("url")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =
            DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false)
        view.webView.setWebViewCallBack(this)
        view.webView.setWebChromeCallBack(this)

        mLoadService?.showCallback(LoadingCallback::class.java)
        view.webView.loadUrl(mUrl.toString())
        mLoadService = LoadSir.getDefault().register(view.refreshLayout)


        view.refreshLayout.setOnRefreshListener {
            view.webView.reload()
        }
        WebViewSetting.getInstance().setWebSettings(view.webView)
        return mLoadService?.loadLayout
    }

    override fun pageStarted(url: String?) {
        Toast.makeText(context,"开始加载",Toast.LENGTH_SHORT).show()
    }

    override fun pageFinished(url: String?) {
        mLoadService?.showSuccess()
        view.refreshLayout.finishRefresh()
    }

    override fun onError(errorCode: Int, description: String, failingUrl: String) {
        Toast.makeText(context,description,Toast.LENGTH_SHORT).show()
        view.refreshLayout.finishRefresh()
    }

    override fun onReceivedTitle(title: String?) {
        if (activity is WebActivity) {
            (activity as WebActivity).updateTitle(title)
        }
    }

}