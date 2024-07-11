package com.mvp.news.view.base

import android.util.Log
import android.view.View

/**
Created by Mr.Chan
Time 2024-06-25
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class LazyBaseFragment : BaseFragment() {


    //在生命周期之前调用
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            canLoad()
        } else {
            dontLoad()
        }
    }

    open fun canLoad() {}
    open fun dontLoad() {}

}