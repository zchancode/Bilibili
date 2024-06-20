package com.mvp.binance.splash.present

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ISplashPresent : com.mvp.binance.splash.present.base.IBasePresent {
    fun getProject()
    fun getProjectList(pageIndex: Int, cid: Int)
}