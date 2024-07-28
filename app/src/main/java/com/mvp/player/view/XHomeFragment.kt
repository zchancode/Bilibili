package com.mvp.player.view

import android.app.Dialog
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mvp.player.model.bean.Video
import com.mvp.player.present.IPlayerPresent
import com.mvp.player.present.PlayerPresent
import com.mvp.player.view.adapter.XVideoListAdapter
import com.mvp.player.view.base.XBaseFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XHomeFragment : XBaseFragment(), IPlayerView {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var videoListView: RecyclerView
    private val present: IPlayerPresent = PlayerPresent(this)
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_home
    }


    override fun initView(view: View) {
        videoListView = view.findViewById<RecyclerView>(R.id.recycler_view)
        videoListView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        present.getVideoList()

    }

    override fun showLoading() {
        dialog = BottomSheetDialog(requireContext())
        dialog.setTitle("Loading")
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()
    }

    override fun hideLoading() {
        dialog.dismiss()
    }

    override fun showVideoList(videoList: List<Video>) {
        dialog.dismiss()
        Toast.makeText(requireContext(), "showVideoList ${videoList.size}", Toast.LENGTH_SHORT).show()
        videoListView.adapter = XVideoListAdapter(requireContext(), videoList)
    }

    override fun showErrorMessage(message: String) {
        dialog.dismiss()
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }
}