package com.rxjava.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bilibili.R
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

class RXActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rx_activity)
        getNews()
    }

    fun getNews() {
        Observable.just("https://whyta.cn/api/tx/bulletin?key=150ff6d47b76")
            .map(object : Function<String, String> {
                override fun apply(t: String): String {
                    val okHttpClient = OkHttpClient()
                    val request = okhttp3.Request.Builder().url(t).build()
                    val call = okHttpClient.newCall(request)
                    val response = call.execute()
                    return response.body?.string() ?: ""
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    Toast.makeText(this@RXActivity, t, Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {
                }
            })

    }
}