package com.mvp.player.model

import com.mvp.player.model.bean.Video
import com.mvp.player.network.NetApi
import com.mvp.player.network.NetApiImp
import io.reactivex.Observable

/**
Created by Mr.Chan
Time 2024-07-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class PlayerModel: IPlayerModel {
    val netApi: NetApi = NetApiImp()
    override fun getVideoList(callback: (Observable<List<Video>>) -> Unit) {
        callback(netApi.getVideoList())
    }
}