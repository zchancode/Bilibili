package com.mvp.binance.login.view

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import com.example.bilibili.R
import com.mvp.binance.login.present.LoginPresent
import com.mvp.binance.splash.view.SplashActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity(), ILoginView, OnClickListener {

    val handler = Handler(Looper.getMainLooper())
    var progressDialog: ProgressDialog? = null
    val ILoginPresent = LoginPresent(this@LoginActivity)
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_binance_activity_login)
        findViewById<Button>(R.id.loginBtn).setOnClickListener(this)
        imageView = findViewById<ImageView>(R.id.imageBitMap)

    }

    override fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("登录中")
        progressDialog?.show()

    }

    override fun hideLoading() {
        progressDialog?.dismiss()
    }


    override fun loginSuccess() {


    }

    override fun loginFailed() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginBtn -> {
                ILoginPresent.login("zchan", "123456")
                Observable.just("https://upload-images.jianshu.io/upload_images/1186132-cd2e1a43b98f2d5b.png?imageMogr2/auto-orient/strip|imageView2/2/w/1065/format/webp")
                    .map(object : Function<String, Bitmap> {
                        override fun apply(t: String): Bitmap {
                            val url = URL(t)
                            val conn = url.openConnection() as HttpURLConnection
                            conn.connectTimeout = 5000
                            val resCode = conn.responseCode
                            if (resCode == 200) {
                                val inputStream = conn.inputStream
                                return BitmapFactory.decodeStream(inputStream)
                            }
                            return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Bitmap> {
                        override fun onComplete() {
                            hideLoading()
                            println("onComplete")
                        }

                        override fun onSubscribe(d: Disposable) {
                            showLoading()
                            println("onSubscribe")
                        }

                        override fun onNext(t: Bitmap) {
                            println("onNext")
                            imageView?.setImageBitmap(t)
                            val intent = Intent(this@LoginActivity, SplashActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onError(e: Throwable) {
                            println("onError")
                        }

                    })
            }
        }
    }

}