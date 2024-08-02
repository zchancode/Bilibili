package com.mvp.player.view

import android.view.View
import com.example.bilibili.R
import com.mvp.player.view.adapter.XMeAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XMeFragment: XBaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_setting
    }

    override fun initView(view: View) {
        val tabView = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.me_tab_layout)
        val tab1 = tabView.newTab()
        val ta1View = layoutInflater.inflate(R.layout.custom_tab_icon, null)
        val ta1Img = ta1View.findViewById<android.widget.ImageView>(R.id.tab_icon)
        ta1Img.setImageResource(R.drawable.me_tab1)
        tab1.customView = ta1View
        tabView.addTab(tab1)

        val tab2 = tabView.newTab()
        val ta2View = layoutInflater.inflate(R.layout.custom_tab_icon, null)
        val ta2Img = ta2View.findViewById<android.widget.ImageView>(R.id.tab_icon)
        ta2Img.setImageResource(R.drawable.me_tab2)
        tab2.customView = ta2View
        tabView.addTab(tab2)



        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.me_recycler_view)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)

        //create colorful bitmap
        val list = mutableListOf<android.graphics.Bitmap>()
        //R.Drawable to bitmap
        val bitmap1 = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.cover1)
        val bitmap2 = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.cover2)
        val bitmap3 = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.cover3)
        val bitmap4 = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.cover4)

        list.add(bitmap1)
        list.add(bitmap2)
        list.add(bitmap3)
        list.add(bitmap4)
        recyclerView.adapter = XMeAdapter(list)


    }
}