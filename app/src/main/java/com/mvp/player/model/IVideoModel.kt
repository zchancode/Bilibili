package com.mvp.player.model

import io.reactivex.Observable
import retrofit2.http.GET

/**
Created by Mr.Chan
Time 2024-08-07
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IVideoModel {
    fun getRandomVideo(callBack: (GetRandomVideoResponse) -> Unit)

    fun searchVideo(query: String, callBack: (SearchResponse) -> Unit)
}