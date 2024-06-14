package com.zchan.splash.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.bilibili.R
import com.jakewharton.rxbinding2.view.RxView
import com.zchan.splash.model.HttpUtil
import com.zchan.splash.model.api.WangAndroidApi
import com.zchan.splash.model.bean.ProjectBean
import com.zchan.splash.model.bean.ProjectItem
import com.zchan.splash.view.base.BaseFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


class ShopFragment : BaseFragment(), View.OnClickListener {
    private var api: WangAndroidApi? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_shop
    }

    @SuppressLint("CheckResult")
    override fun initView(view: View) {
        view.findViewById<Button>(R.id.action).setOnClickListener(this@ShopFragment)
    }


    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.action -> {
                lateinit var processDialog: ProgressDialog
                RxView.clicks(v)
                    .throttleFirst(2, java.util.concurrent.TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Consumer<Any> {
                        override fun accept(t: Any) {
                            api = HttpUtil.getRetrofit().create(WangAndroidApi::class.java)
                            api!!.getProject()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<ProjectBean> {
                                    override fun onSubscribe(d: Disposable) {
                                        processDialog = ProgressDialog.show(
                                            context,
                                            "加载中",
                                            "请稍等",
                                            false,
                                            false
                                        )
                                    }

                                    override fun onNext(t: ProjectBean) {
                                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    override fun onError(e: Throwable) {
                                        processDialog.dismiss()
                                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    override fun onComplete() {
                                        processDialog.dismiss()
                                    }
                                })
                        }
                    })

            }
        }
    }


}
