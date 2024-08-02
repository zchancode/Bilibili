package com.view.dispatch.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.view.dispatch.EventLog

/**
Created by Mr.Chan
Time 2024-07-15
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomView: View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        EventLog.log(id.toString(), "子视图分发事件 ${event?.action}")
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        EventLog.log(id.toString(), "子视图处理事件，但不消耗 ${event?.action}")
        return true
    }

}