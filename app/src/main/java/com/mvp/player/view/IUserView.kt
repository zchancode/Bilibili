package com.mvp.player.view

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IUserView {
    fun onResult(result: Any)

    fun showLoading()

    fun hideLoading()

}