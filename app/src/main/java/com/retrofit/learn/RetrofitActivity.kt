package com.retrofit.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bilibili.R
import com.mvp.news.model.bean.NewsRespond
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RetrofitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)

        //构建一个 Retrofit 对象
        val retrofit = NetUtil.getNetApi()
        retrofit.create(NetApi::class.java).getNews().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<NewsRespond> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: NewsRespond) {
                    println(t)
                }

                override fun onError(e: Throwable) {
                }
            }
        )



    }
}