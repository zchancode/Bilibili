package com.zchan.login.view

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ILoginView {
    fun showLoading()
    fun hideLoading()
    fun loginSuccess()
    fun loginFailed()
}