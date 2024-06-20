package com.mvp.news.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
Created by Mr.Chan
Time 2024-06-18
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomViewGroup : ViewGroup {
    constructor(context: Context) : super(context)
    //在代码里new CustomViewGroup时会调用这个构造方法
    /*
    java.lang.NoSuchMethodException: com.mvp.news.view.custom.CustomViewGroup.<init>
    [class android.content.Context, interface android.util.AttributeSet]
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    //这里要有三个构造方法，因为在xml中使用CustomViewGroup时，安卓框架会调用第二个构造方法 通过反射调用
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    //主题切换时会调用这个构造方法

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        //这个是参考值 如果是match_parent就是父容器的大小
        //如果是wrap_content就要参考孩子的大小然后setMeasuredDimension
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)


        var lineUsedWidth = 0
        var lineCount = 1

        var selfNeedWidth = 0
        var selfNeedHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //测量孩子
            val layoutParams = child.layoutParams//孩子的请求
            val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, layoutParams.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, layoutParams.height)
            //什么是MeasureSpec
            //MeasureSpec是一个32位的int值，高2位表示测量模式，低30位表示测量大小
            //MeasureSpec.EXACTLY：精确模式，当layout_width和layout_height设置为具体值时，如100dp，match_parent，父容器会给孩子一个具体的大小
            //单位是px
            //MeasureSpec.AT_MOST：最大模式，当layout_width和layout_height设置为wrap_content时，最大值为父布局的大小
            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec)//父亲依据孩子的请求来给孩子一个大小

            if (lineUsedWidth + child.measuredWidth > selfWidth) {
                //换行
                Log.e("TAG", "new line : $selfNeedWidth")
                lineUsedWidth = 0
                lineCount++ //用来算总高度
            }
            lineUsedWidth += child.measuredWidth
            selfNeedWidth = Math.max(selfNeedWidth, lineUsedWidth)
            selfNeedHeight = lineCount * child.measuredHeight
            child.setTag(Rect(lineUsedWidth - child.measuredWidth, (lineCount - 1) * child.measuredHeight, lineUsedWidth, lineCount * child.measuredHeight))
            Log.e("TAG", "child message : ${child.measuredWidth} x ${child.measuredHeight} position : ${(child.getTag() as Rect).left} x ${(child.getTag() as Rect).top} x ${(child.getTag() as Rect).right} x ${(child.getTag() as Rect).bottom}")
        }
        Log.e("TAG", "grandpa suggestion : $selfWidth x $selfHeight")
        val realNeedWidth = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) selfWidth else selfNeedWidth
        val realNeedHeight = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) selfHeight else selfNeedHeight

        setMeasuredDimension(realNeedWidth, realNeedHeight)
        Log.e("TAG", "parent message : $selfNeedWidth x $selfNeedHeight")
        Log.e("TAG", "parent real message : $realNeedWidth x $realNeedHeight")


    }
    //每一个view的大小由父亲和孩子同时决定，所以要用到onMeasure方法
    //测量孩子：我们根据layout_width和layout_height来获得孩子的大小

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val rect = child.getTag() as Rect
            child.layout(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }


}