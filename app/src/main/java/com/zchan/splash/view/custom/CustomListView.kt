package com.zchan.splash.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

/**
Created by Mr.Chan
Time 2024-06-12
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomListView : ListView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}