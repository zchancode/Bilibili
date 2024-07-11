package com.mvp.news.view

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.bilibili.R
import com.mvp.news.view.base.BaseFragment
import com.mvp.news.view.base.LazyBaseFragment

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class MeFragment: LazyBaseFragment() {

    override fun canLoad() {
        super.canLoad()
        Log.e("TAG", "MeFragment 加载数据")
    }

    override fun dontLoad() {
        super.dontLoad()
        Log.e("TAG", "MeFragment 不加载数据")
    }


    override fun getLayoutId(): Int {
        return R.layout.mvp_news_activity_me
    }

    override fun initView(view: View) {
        val button = view.findViewById<View>(R.id.clickAbleButton)
        button.setOnClickListener {
            Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show()
        }

        button.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Toast.makeText(context, "触摸了", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        val layout = view.findViewById<LinearLayout>(R.id.eventLayout)
        layout.setOnClickListener {
            Toast.makeText(context, "eventLayout点击了", Toast.LENGTH_SHORT).show()
        }

        val aBtn = view.findViewById<View>(R.id.aBtn)
        aBtn.setOnClickListener {
            Toast.makeText(context, "aBtn点击了", Toast.LENGTH_SHORT).show()
        }


        /*事件的处理： 事件会先执行onTouch 然后根据返回值判断是否执行onTouchEvent这里面有很多事件
        * click 就是其中之一
        * 事件的分发： xxxx*/
    }

    override fun onResume() {
        super.onResume()
        canLoad()
    }

    override fun onPause() {
        super.onPause()
        dontLoad()
    }
}