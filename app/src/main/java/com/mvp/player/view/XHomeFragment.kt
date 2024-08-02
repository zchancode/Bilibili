package com.mvp.player.view

import android.app.Dialog
import android.service.media.MediaBrowserService.BrowserRoot
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.bilibili.R
import com.example.playercore.PlayInterface
import com.example.playercore.PlayerSurface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mvp.player.view.adapter.XHomeCommentAdapter
import com.mvp.player.view.adapter.XVideoListAdapter
import com.mvp.player.view.base.XBaseFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.zip.Inflater

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XHomeFragment : XBaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_home
    }

    override fun onPause() {
        super.onPause()
        PlayInterface.stopPlay()
    }


    override fun initView(view: View) {
        val videoListView = view.findViewById<ViewPager2>(R.id.view_pager)
        videoListView.adapter = XVideoListAdapter(this)
        videoListView.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        videoListView.orientation = ViewPager2.ORIENTATION_VERTICAL


    }



}