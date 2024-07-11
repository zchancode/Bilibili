package com.mvp.news.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.DragEvent


/**
Created by Mr.Chan
Time 2024-06-18
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(selfWidth, selfHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = 0xff00ff00.toInt()
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
        val path = Path()
        path.moveTo(100f, 100f)
        path.lineTo(200f, 200f)
        path.lineTo(200f, 300f)
        path.lineTo(100f, 300f)
        path.close()
        canvas?.drawPath(path, paint)
    }


}