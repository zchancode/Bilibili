package com.mvp.player.model

import com.mvp.player.App
import com.mvp.player.network.RetrofitManager
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
Created by Mr.Chan
Time 2024-08-07
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class VideoModel: IVideoModel {
    override fun getRandomVideo(callBack: (GetRandomVideoResponse) -> Unit) {
        RetrofitManager.getRetrofit(App.instance.getToken()).create(VideoService::class.java)
            .getRandomVideo()
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GetRandomVideoResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: GetRandomVideoResponse) {
                    callBack(t)
                }

                override fun onError(e: Throwable) {
                    callBack(GetRandomVideoResponse(false, e.message ?: "error", Video("", "", "", "", "", "")))
                }

                override fun onComplete() {
                }
            })
    }

    override fun searchVideo(query: String, callBack: (SearchResponse) -> Unit) {
        RetrofitManager.getRetrofit(App.instance.getToken()).create(VideoService::class.java)
            .searchVideo(SearchRequest(query))
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SearchResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: SearchResponse) {
                    callBack(t)
                }

                override fun onError(e: Throwable) {
                    callBack(SearchResponse(false, e.message ?: "error", listOf()))
                }

                override fun onComplete() {
                }
            })
    }


}