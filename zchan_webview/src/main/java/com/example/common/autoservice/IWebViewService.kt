package com.example.common.autoservice

import android.content.Context
import androidx.fragment.app.Fragment

/**
Created by Mr.Chan
Time 2024-07-01
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IWebViewService { //依赖倒置原则：面向接口编程
    fun startWebActivity(context: Context?, url: String, title: String, hasTitle: Boolean)
    fun getWebViewFragment(url: String): Fragment
    fun loadLocalPage(context: Context?, url: String, title: String, hasTitle: Boolean)
}