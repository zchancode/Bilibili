package com.mvp.news.model

import com.mvp.news.model.api.BulletinApi
import com.mvp.news.model.bean.NewsRespond
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class NewsModel : INewsModel {
    override fun getNews(listener: (NewsRespond) -> Unit) {
        HttpUtil.getRetrofit().create(BulletinApi::class.java)
            .getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<NewsRespond> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: NewsRespond) {
                    listener(t)
                }

                override fun onError(e: Throwable) {
                }
            })
    }
}