package com.mvp.player.network

import com.mvp.player.model.bean.Video
import io.reactivex.Observable

/**
Created by Mr.Chan
Time 2024-07-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface NetApi {
    fun getVideoList(): Observable<List<Video>>
}