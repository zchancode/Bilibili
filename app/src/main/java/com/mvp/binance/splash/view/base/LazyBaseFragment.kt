package com.mvp.binance.splash.view.base

import android.widget.Toast

/**
Created by Mr.Chan
Time 2024-06-25
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class LazyBaseFragment: BaseFragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }
}