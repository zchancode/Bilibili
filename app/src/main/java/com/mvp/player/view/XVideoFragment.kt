package com.mvp.player.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.example.playercore.PlayInterface
import com.example.playercore.PlayerSurface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mvp.player.view.adapter.XHomeCommentAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XVideoFragment : XBaseFragment(), IVideoListView {

    private lateinit var commentDialog: BottomSheetDialog
    private lateinit var shareDialog: BottomSheetDialog
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_video
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: $this")
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: $this")
    }

    override fun onPause() {
        super.onPause()
        Log.e("TAG", "onPause: $this")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy: $this")
    }


    override fun initView(view: View) {
        val commentBtn = view.findViewById<ImageView>(R.id.comment_btn)
        val shareBtn = view.findViewById<ImageView>(R.id.share_btn)
        commentBtn.setOnClickListener {
            showCommentDialog()
        }
        shareBtn.setOnClickListener {
            showShareDialog()
        }


        val playerSurface = view.findViewById<PlayerSurface>(R.id.player_surface)
        playerSurface.setOnSurfaceCreated {
            PlayInterface.setSurface(it.surface)
            PlayInterface.stopPlay()
            PlayInterface.startPlay("/data/data/com.example.bilibili/test.mp4")
        }
    }

    override fun showCommentDialog() {
        commentDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.x_player_fragment_video_comment, null)
        commentDialog.setContentView(view)

        val listView = view.findViewById<RecyclerView>(R.id.recyclerView)
        listView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        listView.adapter = XHomeCommentAdapter(listOf("comment1", "comment2", "comment3"))

        commentDialog.show()
    }




    override fun closeCommentDialog() {
        commentDialog.dismiss()
    }

    override fun showShareDialog() {
        shareDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.x_player_fragment_video_share, null)
        shareDialog.setContentView(view)
        shareDialog.show()
    }

    override fun closeShareDialog() {
        shareDialog.dismiss()
    }

}