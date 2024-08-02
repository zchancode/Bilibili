package com.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup

/**
Created by Mr.Chan
Time 2024-07-24
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class ViewGroupLayout : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var neededWidth = 0
        var neededHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            neededHeight += child.measuredHeight
            neededWidth = Math.max(neededWidth, child.measuredWidth)
        }

        setMeasuredDimension(resolveSize(neededWidth, widthMeasureSpec), resolveSize(neededHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(0, top, child.measuredWidth, top + child.measuredHeight)
            top += child.measuredHeight
        }
    }

}