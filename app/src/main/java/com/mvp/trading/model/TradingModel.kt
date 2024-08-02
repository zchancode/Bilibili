package com.mvp.trading.model

import com.mvp.trading.network.KlinesApi
import com.mvp.trading.network.RetrofitManager

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class TradingModel : ITradingModel {
    override fun calculate(callBack: (String) -> Unit) {
        val klinesApi = RetrofitManager.getRetrofit().create(KlinesApi::class.java)
        val response = klinesApi.getKlines("SOLUSDT", "4h", 20)
        response.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.observers.DisposableObserver<String>() {
                override fun onNext(t: String) {
                    callBack(t)
                }

                override fun onError(e: Throwable) {
                    callBack(e.message.toString())
                }

                override fun onComplete() {
                }
            })

    }

}