package com.mvp.player.present

import com.mvp.player.model.IVideoModel
import com.mvp.player.model.VideoModel
import com.mvp.player.view.IVideoView

/**
Created by Mr.Chan
Time 2024-08-07
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class VideoPresent : IVideoPersent {

    private var iVideoView: IVideoView
    private var iVideoModel: IVideoModel = VideoModel()

    constructor(iVideoView: IVideoView) {
        this.iVideoView = iVideoView
    }

    override fun getRandomVideo() {
        iVideoModel.getRandomVideo {
            iVideoView.onResult(it)
        }
    }

    override fun searchVideo(query: String) {
        iVideoModel.searchVideo(query) {
            iVideoView.onResult(it)
        }
    }
}