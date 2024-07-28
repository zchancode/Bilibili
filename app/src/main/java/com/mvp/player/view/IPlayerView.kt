package com.mvp.player.view

import com.mvp.player.model.bean.Video

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IPlayerView {
    fun showLoading()
    fun hideLoading()

    fun showVideoList(videoList: List<Video>)

    fun showErrorMessage(message: String)

}