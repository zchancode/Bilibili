package com.zchan.splash.view.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
Created by Mr.Chan
Time 2024-06-11
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class AnalyticsView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}