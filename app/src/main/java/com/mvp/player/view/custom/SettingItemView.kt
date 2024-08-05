package com.mvp.player.view.custom

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.example.bilibili.R
import java.util.zip.Inflater

/**
Created by Mr.Chan
Time 2024-08-04
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SettingItemView : android.view.ViewGroup {

    private lateinit var leftText: android.widget.TextView
    private lateinit var rightText: android.widget.TextView
    private lateinit var rightIcon: android.widget.ImageView

    constructor(context: android.content.Context) : super(context) {
        init()
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(
        context,
        attrs
    ) {
        init()
        //get custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemViewAttrs)
        val leftText = typedArray.getString(R.styleable.SettingItemViewAttrs_leftText)
        val rightText = typedArray.getString(R.styleable.SettingItemViewAttrs_rightText)


        val leftTextVisible =
            typedArray.getBoolean(R.styleable.SettingItemViewAttrs_leftTextVisible, true)
        val rightTextVisible =
            typedArray.getBoolean(R.styleable.SettingItemViewAttrs_rightTextVisible, true)
        val rightIconVisible =
            typedArray.getBoolean(R.styleable.SettingItemViewAttrs_rightIconVisible, true)

        //set custom attributes
        setLeftText(leftText ?: "Default Left Text")
        setRightText(rightText ?: "Default Right Text")

        setLeftTextVisible(leftTextVisible)
        setRightTextVisible(rightTextVisible)
        setRightIconVisible(rightIconVisible)

    }

    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        val viewGroup =
            android.view.LayoutInflater.from(context).inflate(R.layout.setting_item, this, false)
        leftText = viewGroup.findViewById<android.widget.TextView>(R.id.leftText)
        rightText = viewGroup.findViewById<android.widget.TextView>(R.id.rightText)
        rightIcon = viewGroup.findViewById<android.widget.ImageView>(R.id.rightIcon)
        addView(viewGroup)
    }


    fun setLeftTextVisible(visible: Boolean) {
        leftText.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
    }

    fun setRightTextVisible(visible: Boolean) {
        rightText.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
    }

    public fun setLeftText(text: String) {
        leftText.text = text
    }

    public fun setRightText(text: String) {
        rightText.text = text
    }

    public fun setRightIcon(resId: Int) {
        rightIcon.setImageResource(resId)
        rightIcon.background = null
    }

    public fun setRightIconVisible(visible: Boolean) {
        rightIcon.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
    }

    fun getLeftText(): String {
        return leftText.text.toString()
    }

    fun getRightText(): String {
        return rightText.text.toString()
    }

    fun getRightIcon(): android.graphics.drawable.Drawable {
        return rightIcon.drawable
    }

    fun setItemOnClickListener(listener: android.view.View.OnClickListener) {
        setOnClickListener(listener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var neededWidth = 0
        var neededHeight = 0

        getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec)

        neededWidth = getChildAt(0).measuredWidth
        neededHeight = getChildAt(0).measuredHeight

        val realWidth = resolveSize(neededWidth, widthMeasureSpec)
        val realHeight = resolveSize(neededHeight, heightMeasureSpec)
        setMeasuredDimension(realWidth, realHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0)
        child.layout(0, 0, child.measuredWidth, child.measuredHeight)
    }
}