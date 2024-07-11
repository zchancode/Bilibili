package com.mvp.binance.splash.model

import com.mvp.binance.splash.model.api.WangAndroidApi
import com.mvp.binance.splash.model.api.WangAndroidApiRespond
import com.mvp.binance.splash.model.bean.ProjectBean
import com.mvp.binance.splash.model.bean.ProjectItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SplashModel: ISplashModel {
    override fun getProject(callback: (ProjectBean) -> Unit, error: (Throwable) -> Unit) {
        val api = HttpUtil.getRetrofit().create(WangAndroidApi::class.java)
        api.getProject()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : WangAndroidApiRespond<ProjectBean>() {
                override fun onFail(e: Throwable) {
                    error(e)
                }

                override fun onSuccess(projectBean: ProjectBean) {
                    callback(projectBean)
                }
            })
    }

    override fun getProjectList(pageIndex: Int, cid: Int, callback: (ProjectItem) -> Unit, error: (Throwable) -> Unit) {

    }
}