package com.mvp.binance.splash.model

import com.mvp.binance.splash.model.bean.ProjectBean
import com.mvp.binance.splash.model.bean.ProjectItem

/**
Created by Mr.Chan
Time 2024-06-16
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ISplashModel {
    fun getProject(callback: (ProjectBean) -> Unit, error: (Throwable) -> Unit)
    fun getProjectList(pageIndex: Int, cid: Int, callback: (ProjectItem) -> Unit, error: (Throwable) -> Unit)
}