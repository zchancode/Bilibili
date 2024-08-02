package com.view.dispatch.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.view.dispatch.EventLog

/**
Created by Mr.Chan
Time 2024-07-15
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomViewGroup: ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        EventLog.log(id.toString(), "父视图分发事件 ${ev?.action}")
        return super.dispatchTouchEvent(ev)

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        EventLog.log(id.toString(), "父视图不拦截 ${ev?.action}")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        EventLog.log(id.toString(), "父视图处理，但不消耗 ${event?.action}")
        return false
    }

}