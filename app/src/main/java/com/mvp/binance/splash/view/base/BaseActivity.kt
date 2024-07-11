package com.mvp.binance.splash.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bilibili.R
import com.mvp.binance.splash.present.base.IBasePresent

/**
Created by Mr.Chan
Time 2024-06-06
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class BaseActivity : AppCompatActivity() {
    private fun setCustomDensity() {
        val appDisplayMetrics = resources.displayMetrics
        val targetDensity = appDisplayMetrics.widthPixels / 360f
        val targetScaledDensity = targetDensity * (resources.displayMetrics.scaledDensity / resources.displayMetrics.density)
        val targetDensityDpi = (160 * targetDensity).toInt()

        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Main)
        setCustomDensity()
        super.onCreate(savedInstanceState)
    }

}