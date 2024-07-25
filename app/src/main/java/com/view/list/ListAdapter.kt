package com.view.list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import kotlin.random.Random

/**
Created by Mr.Chan
Time 2024-07-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>(){
    private lateinit var context: Context
    private lateinit var newsData: ArrayList<NewsData>

    constructor(context: Context, newsData: ArrayList<NewsData>): this() {
        this.context = context
        this.newsData = newsData
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.listContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.text = newsData[position].content
        holder.content.setTextColor(Color.GRAY)
        holder.content.setBackgroundColor(Color.GRAY)
        holder.content.setOnClickListener {
            Toast.makeText(context, newsData[position].content, Toast.LENGTH_SHORT).show()
        }
    }
}