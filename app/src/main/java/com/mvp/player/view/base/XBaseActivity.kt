package com.mvp.player.view.base

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
abstract class XBaseActivity: AppCompatActivity() {

    /*
    * setCustomDensity() 是一个自定义的方法，用于设置屏幕适配
    * 通过修改 density、scaledDensity、densityDpi 三个属性来实现适配
    *
    * widthPixels 是屏幕的宽度，360f 是设计图的宽度
    */

    private fun setCustomDensity() {
        val appDisplayMetrics = resources.displayMetrics
        val targetDensity = appDisplayMetrics.widthPixels / 414f
        val targetScaledDensity = targetDensity * (resources.displayMetrics.scaledDensity / resources.displayMetrics.density)
        val targetDensityDpi = (160 * targetDensity).toInt()
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCustomDensity()


    }
}