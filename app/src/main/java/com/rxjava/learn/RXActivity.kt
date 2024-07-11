package com.rxjava.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bilibili.R
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class RXActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rx_activity)
    }

    fun downLoadImage() {
        Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.onNext("Msg")
            }
        }).subscribe(object : Observer<String> {
            override fun onComplete() {
                Toast.makeText(this@RXActivity, "onComplete", Toast.LENGTH_SHORT).show()
            }

            override fun onSubscribe(d: Disposable) {
                Toast.makeText(this@RXActivity, "onSubscribe", Toast.LENGTH_SHORT).show()
            }

            override fun onNext(t: String) {
                Toast.makeText(this@RXActivity, "onNext $t", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Throwable) {
                Toast.makeText(this@RXActivity, "onError", Toast.LENGTH_SHORT).show()
            }
        })




    }
}