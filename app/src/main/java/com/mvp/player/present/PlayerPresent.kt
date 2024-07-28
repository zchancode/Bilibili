package com.mvp.player.present

import android.util.Log
import com.mvp.player.model.IPlayerModel
import com.mvp.player.model.PlayerModel
import com.mvp.player.model.bean.Video
import com.mvp.player.view.IPlayerView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
Created by Mr.Chan
Time 2024-07-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class PlayerPresent(private var view: IPlayerView) : IPlayerPresent {
    private var model: IPlayerModel = PlayerModel()

    override fun getVideoList() {
        view.showLoading()
        model.getVideoList {
            //io
            it.subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Video>> {
                    override fun onComplete() {
                        view.hideLoading()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: List<Video>) {
                        view.showVideoList(t)
                    }

                    override fun onError(e: Throwable) {
                        view.hideLoading()
                        view.showErrorMessage(e.message.toString())
                    }
                })
        }
    }
}