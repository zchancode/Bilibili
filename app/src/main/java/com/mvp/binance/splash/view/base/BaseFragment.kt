package com.mvp.binance.splash.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mvp.binance.splash.present.base.IBasePresent

/**
Created by Mr.Chan
Time 2024-06-06
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class BaseFragment : Fragment() {
    abstract fun getLayoutId(): Int
    abstract fun initView(view: View)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        initView(view)
        return view
    }


}