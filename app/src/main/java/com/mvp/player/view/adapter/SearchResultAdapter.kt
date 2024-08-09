package com.mvp.player.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.mvp.player.model.Video

class SearchResultAdapter(private val searchResultList: ArrayList<Video>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder: RecyclerView.ViewHolder{
        constructor(itemView: View) : super(itemView)
        val title = itemView.findViewById<TextView>(R.id.videoTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.x_player_search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = searchResultList[position].title
    }

}
