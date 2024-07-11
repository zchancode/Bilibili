package com.mvp.binance.splash.model.api

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class WangAndroidApiRespond<T : Any> : io.reactivex.Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: io.reactivex.disposables.Disposable) {
    }

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        onFail(e)
    }

    abstract fun onFail(e: Throwable)
    abstract fun onSuccess(t: T)
}