package com.mvp.player.view

import com.mvp.player.model.GetRandomVideoResponse

/**
Created by Mr.Chan
Time 2024-07-31
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IVideoView {

    fun onResult(result: Any)

    fun onShowLoading()

    fun onHideLoading()


}