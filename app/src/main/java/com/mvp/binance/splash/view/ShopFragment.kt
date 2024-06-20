package com.mvp.binance.splash.view

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.bilibili.R
import com.jakewharton.rxbinding2.view.RxView
import com.mvp.binance.splash.model.bean.ProjectBean
import com.mvp.binance.splash.present.ISplashPresent
import com.mvp.binance.splash.present.SplashPresent
import com.mvp.binance.splash.view.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer


class ShopFragment : BaseFragment(), View.OnClickListener, ISplashView {
    lateinit var processDialog: ProgressDialog

    override fun getLayoutId(): Int {
        return R.layout.mvp_binance_fragment_shop
    }

    @SuppressLint("CheckResult")
    override fun initView(view: View) {
        view.findViewById<Button>(R.id.action).setOnClickListener(this@ShopFragment)
    }


    @SuppressLint("CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.action -> {
                RxView.clicks(v)
                    .throttleFirst(2, java.util.concurrent.TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Consumer<Any> {
                        override fun accept(t: Any) {
                            val present: ISplashPresent = SplashPresent(this@ShopFragment)
                            present.getProject()
                        }
                    })

            }
        }
    }

    override fun showLoading() {
        processDialog = ProgressDialog(context)
        processDialog.setTitle("正在加载中")
        processDialog.show()
    }

    override fun hideLoading() {
        processDialog.dismiss()
    }

    override fun showObj(t: Any) {
        t as ProjectBean
        Toast.makeText(context, "请求成功 ${t.data} ", Toast.LENGTH_SHORT).show()
    }

    override fun showError(t: Throwable) {
        Toast.makeText(context, "请求失败 ${t.message} ", Toast.LENGTH_SHORT).show()

    }


}
