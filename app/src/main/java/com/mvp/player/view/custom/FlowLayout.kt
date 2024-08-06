package com.mvp.player.view.custom

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup

/**
Created by Mr.Chan
Time 2024-08-06
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class FlowLayout : ViewGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val neededWidth = MeasureSpec.getSize(widthMeasureSpec)
        var neededHeight = 0
        var lineWidth = 0
        var lineHeight = 0
        var lineCount = 0
        var firstLineHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (lineWidth + child.measuredWidth > MeasureSpec.getSize(widthMeasureSpec)) {
                neededHeight += lineHeight
                lineCount++
                lineWidth = child.measuredWidth
                lineHeight = child.measuredHeight
                child.setTag(Rect(0, neededHeight, child.measuredWidth, neededHeight + child.measuredHeight))
            } else {
                lineWidth += child.measuredWidth
                lineHeight = Math.max(lineHeight, child.measuredHeight)
                if (lineCount == 0) {
                    firstLineHeight = lineHeight
                }
                child.setTag(Rect(lineWidth - child.measuredWidth, neededHeight, lineWidth, neededHeight + child.measuredHeight))
            }

        }
        setMeasuredDimension(neededWidth, if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) MeasureSpec.getSize(heightMeasureSpec) else neededHeight + firstLineHeight)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val rect = child.getTag() as Rect
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
        }
    }


}