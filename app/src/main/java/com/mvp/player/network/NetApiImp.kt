package com.mvp.player.network

import com.mvp.player.model.bean.Video
import io.reactivex.Observable

/**
Created by Mr.Chan
Time 2024-07-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class NetApiImp: NetApi {
    val base = "http://192.168.230.1:3000/"
    override fun getVideoList(): Observable<List<Video>> {

        return RetrofitManager.getRetrofit(base).create(VideoService::class.java).getVideoList()
    }


}