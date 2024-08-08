package com.mvp.player.view

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.example.playercore.PlayInterface
import com.example.playercore.PlayerSurface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mvp.player.model.GetRandomVideoResponse
import com.mvp.player.present.VideoPresent
import com.mvp.player.view.adapter.XHomeCommentAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XVideoFragment : XBaseFragment(), IVideoView {

    private lateinit var commentDialog: BottomSheetDialog
    private lateinit var shareDialog: BottomSheetDialog
    private lateinit var loadingDialog: ProgressDialog
    private lateinit var videoInfo: TextView
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
        val demuxLog = view.findViewById<TextView>(R.id.demuxLog)
        val decodeLog = view.findViewById<TextView>(R.id.decodeLog)
        videoInfo = view.findViewById<TextView>(R.id.videoInfo)

        com.example.playercore.Log.setLogListener {
            Handler(Looper.getMainLooper()).post {
                if (it.startsWith("fileDemux")){
                    demuxLog.text = it
                }

                if (it.startsWith("videoDecode")){
                    decodeLog.text = it
                }

                if(decodeLog.text.equals("videoDecode: 90") && demuxLog.text.equals("fileDemux: finishLoad")){
                    demuxLog.text = "fileDemux: reLoad"
                    //TODO 这里要完成循环播放 seek 没用 replay卡顿 要是放以前我愿意花两天来研究，只是因为热爱
                    //TODO 但是现在我要找tm的工作，我先完成其他部分
                }
            }
        }

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
            val videoPresent = VideoPresent(this)
            videoPresent.getRandomVideo()

        }
    }

    override fun onResult(result: Any) {
        val result = result as GetRandomVideoResponse
        if (!result.success) {
            Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
            return
        }
        videoInfo.text = result.data.url
        Thread {
            PlayInterface.startPlay(result.data.url)
        }.start()

    }

    override fun onShowLoading() {
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog.show()
    }

    override fun onHideLoading() {
        loadingDialog.dismiss()
    }


    fun showCommentDialog() {
        commentDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.x_player_fragment_video_comment, null)
        commentDialog.setContentView(view)

        val listView = view.findViewById<RecyclerView>(R.id.recyclerView)
        listView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        listView.adapter = XHomeCommentAdapter(listOf("comment1", "comment2", "comment3"))

        commentDialog.show()
    }


    fun closeCommentDialog() {
        commentDialog.dismiss()
    }

    fun showShareDialog() {
        shareDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.x_player_fragment_video_share, null)
        shareDialog.setContentView(view)
        shareDialog.show()
    }

    fun closeShareDialog() {
        shareDialog.dismiss()
    }

}