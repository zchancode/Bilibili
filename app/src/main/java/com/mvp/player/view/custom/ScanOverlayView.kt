package com.mvp.player.view.custom

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
Created by Mr.Chan
Time 2024-08-09
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class ScanOverlayView:View {
    private val paint = Paint()
    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs){
        paint.color = Color.parseColor("#80000000") // 半透明灰色
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 获取 View 的宽高
        val width = width
        val height = height

        // 计算正方形的边长，取宽和高中的最小值
        val squareSize = (width * 0.7f).coerceAtMost(height * 0.7f)

        // 计算正方形的位置，使其居中
        val rectLeft = (width - squareSize) / 2
        val rectTop = (height - squareSize) / 2
        val rectRight = rectLeft + squareSize
        val rectBottom = rectTop + squareSize

        // 绘制四个区域以形成中间透明的正方形
        canvas.drawRect(0f, 0f, width.toFloat(), rectTop, paint)
        canvas.drawRect(0f, rectTop, rectLeft, rectBottom, paint)
        canvas.drawRect(rectRight, rectTop, width.toFloat(), rectBottom, paint)
        canvas.drawRect(0f, rectBottom, width.toFloat(), height.toFloat(), paint)
    }
}