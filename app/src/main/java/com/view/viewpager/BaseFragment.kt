package com.view.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
Created by Mr.Chan
Time 2024-07-23
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class BaseFragment: Fragment() {

    abstract fun initView(view: View)

    abstract fun getLayoutId(): Int


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