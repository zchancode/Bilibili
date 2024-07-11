package com.mvp.binance.splash.view

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ISplashView {
    fun showLoading()
    fun hideLoading()
    fun showObj(t : Any)
    fun showError(t: Throwable)
}