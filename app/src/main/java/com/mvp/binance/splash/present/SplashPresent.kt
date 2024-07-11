package com.mvp.binance.splash.present

import com.mvp.binance.splash.model.ISplashModel
import com.mvp.binance.splash.model.SplashModel
import com.mvp.binance.splash.present.ISplashPresent
import com.mvp.binance.splash.view.ISplashView

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SplashPresent(var splashView: ISplashView?) : ISplashPresent {

    private var splashModel: ISplashModel

    init {
        splashModel = SplashModel()
    }

    override fun getProject() {
        splashView?.showLoading()
        splashModel.getProject({ projectBean ->
            splashView?.hideLoading()
            splashView?.showObj(projectBean)
        }, { throwable ->
            splashView?.hideLoading()
            splashView?.showError(throwable)
        })
    }

    override fun getProjectList(pageIndex: Int, cid: Int) {
    }

    override fun attachView() {
        TODO("Not yet implemented")
    }

    override fun detachView() {
        splashView = null
    }
}