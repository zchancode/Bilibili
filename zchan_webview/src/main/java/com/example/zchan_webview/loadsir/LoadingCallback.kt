package com.example.zchan_webview.loadsir

/**
Created by Mr.Chan
Time 2024-07-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
//loadsir
import com.example.zchan_webview.R
import com.kingja.loadsir.callback.Callback
class LoadingCallback: Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }
}