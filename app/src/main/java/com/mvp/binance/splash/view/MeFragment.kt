package com.mvp.binance.splash.view

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import com.example.bilibili.R
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mvp.binance.splash.view.base.BaseFragment
import com.mvp.binance.splash.view.base.LazyBaseFragment
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

class MeFragment : LazyBaseFragment(), View.OnClickListener {
    override fun getLayoutId(): Int {
        return R.layout.mvp_binance_fragment_me
    }

    class Obj{
        @SerializedName("N")
        var name: String = "zhang"
        @SerializedName("A")
        var age: Int = 18
        @Expose(serialize = false, deserialize = false)
        var money = 100
    }


    override fun initView(view: View) {
        view.findViewById<View>(R.id.writeIO).setOnClickListener(this@MeFragment)
        view.findViewById<View>(R.id.paresJson).setOnClickListener(this@MeFragment)

    }


    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.paresJson ->{
                val gson = Gson()
                val obj = Obj()
                val json = gson.toJson(obj)
                Toast.makeText(context, json, Toast.LENGTH_SHORT).show()
            }

            R.id.writeIO -> {

                Observable.create(object : ObservableOnSubscribe<String> {
                    override fun subscribe(emitter: ObservableEmitter<String>) {
                        Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()
                        emitter.onNext("1")
                    }
                }).map(object: Function<String, String> {
                    override fun apply(t: String): String {
                        Toast.makeText(context, "2", Toast.LENGTH_SHORT).show()
                        return "2"
                    }
                }).subscribe(object : Observer<String> {
                    override fun onComplete() {
                        Toast.makeText(context, "onComplete", Toast.LENGTH_SHORT).show()
                    }

                    override fun onSubscribe(d: Disposable) {
                        Toast.makeText(context, "onSubscribe", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNext(t: String) {
                        Toast.makeText(context, "onNext", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show()
                    }

                })

            }
        }
    }

}
