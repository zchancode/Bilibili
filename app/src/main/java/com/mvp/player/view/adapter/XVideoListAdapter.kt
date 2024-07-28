package com.mvp.player.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.mvp.player.model.bean.Video
import java.util.zip.Inflater

class XVideoListAdapter : RecyclerView.Adapter<XVideoListAdapter.ViewHolder> {
    private var list: List<Video> = ArrayList()
    private var context: Context? = null

    constructor(context: Context, list: List<Video>) {
        this.list = list
        this.context = context
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): XVideoListAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.x_player_item_video, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: XVideoListAdapter.ViewHolder, position: Int) {
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = list[position].name
    }

}
