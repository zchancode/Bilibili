package com.mvp.player.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R

class XHomeCommentAdapter : RecyclerView.Adapter<XHomeCommentAdapter.ViewHolder> {

    private var comments: List<String> = ArrayList()
    constructor(comments: List<String>) : super() {
        this.comments = comments
    }

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val comment: android.widget.TextView = view.findViewById(R.id.tv_comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.main_comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.comment.text = comments[position]
    }

}
