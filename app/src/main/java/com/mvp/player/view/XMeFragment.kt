package com.mvp.player.view

import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.bilibili.R
import com.mvp.player.App
import com.mvp.player.model.GetUserInfoResponse
import com.mvp.player.model.UserModel
import com.mvp.player.view.adapter.XMeAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XMeFragment : XBaseFragment(), IUserView {
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_setting
    }


    private lateinit var dialog: ProgressDialog
    private lateinit var root: View
    override fun initView(view: View) {
        root = view
        val tabView =
            view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.me_tab_layout)
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


        val recyclerView =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.me_recycler_view)
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


        val editProfile = view.findViewById<android.widget.TextView>(R.id.editProfile)
        editProfile.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }
        val userPresenter = com.mvp.player.present.UserPresent(this)
        userPresenter.getUserInfo()


        val addFriend = view.findViewById<android.widget.ImageView>(R.id.addFriend)
        addFriend.setOnClickListener {
            startActivity(Intent(context, FindActivity::class.java))
        }


    }

    override fun onResult(result: Any) {
        val user = result as GetUserInfoResponse
        if (user.success) {
            root.findViewById<android.widget.TextView>(R.id.username).text = user.data.username
        }
        Log.d("XMeFragment", "onResult: $result")
    }

    override fun showLoading() {
        dialog = ProgressDialog(context)
        dialog.setTitle("加载中")
        dialog.show()
    }

    override fun hideLoading() {
        dialog.dismiss()
    }
}