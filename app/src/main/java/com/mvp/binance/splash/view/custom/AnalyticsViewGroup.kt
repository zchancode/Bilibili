package com.mvp.binance.splash.view.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.example.bilibili.R

/**
Created by Mr.Chan
Time 2024-06-11
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class AnalyticsViewGroup : ViewGroup {

    fun dp2px(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    class AnalyticsItemData {
        var month: String = ""
        var select: Boolean = false
        var dollar: Float = .0f
        constructor(select: Boolean, dollar: Float, month: String = "") {
            this.select = select
            this.dollar = dollar
            this.month = month
        }

    }


    var analyticsDatas : ArrayList<AnalyticsItemData> = ArrayList()

    init {
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 1234.0f, month = "Jan"))
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 2234.0f, month = "Feb"))
        analyticsDatas.add(AnalyticsItemData(select = true, dollar = 5234.0f, month = "Mar"))
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 2234.0f, month = "Apr"))
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 1000.0f, month = "May"))
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 2900.0f, month = "Jun"))
        analyticsDatas.add(AnalyticsItemData(select = false, dollar = 3900.0f, month = "Jul"))
        for(i in analyticsDatas){
            val view = LayoutInflater.from(context).inflate(R.layout.analytics_item, this, false)

            view.findViewById<TextView>(R.id.itemMonth).also {
                it.setTextColor(if(i.select) Color.parseColor("#8234F8") else Color.parseColor("#A3A3A3"))
                it.text = i.month
            }

            view.findViewById<TextView>(R.id.itemDolar).also {
                it.setTextColor(if(i.select) Color.parseColor("#8234F8") else Color.parseColor("#A3A3A3"))
                it.text = "$${i.dollar}"
            }
            view.findViewById<TextView>(R.id.dollarBar).also {
                it.background = if(i.select) context.getDrawable(R.drawable.analytics_item_shape) else context.getDrawable(R.drawable.analytics_item_shape2)
                val maxDollar = analyticsDatas.maxByOrNull { it.dollar }?.dollar ?: 1.0f
                it.layoutParams.height = (i.dollar / maxDollar * 250).toInt()

            }


            addView(view)

        }

    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (child in children) {
            val layoutParams = child.layoutParams
            val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,child.paddingLeft + child.paddingRight, layoutParams.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, child.paddingTop + child.paddingBottom, layoutParams.height)

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            Log.d("AnalyticsViewGroup", "onMeasure: ${child.measuredWidth} ${child.measuredHeight}")
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (child in children) {
            child.layout(children.indexOf(child) * child.measuredWidth, (b-t) - child.measuredHeight, (children.indexOf(child) + 1) * child.measuredWidth, b)
        }
    }


}