package com.view.viewpager2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import java.util.zip.Inflater

/**
Created by Mr.Chan
Time 2024-07-25
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class ViewPager2Adapter : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {

    private var list: List<String> = ArrayList()
    private var context: Context? = null

    constructor(context: Context, list: List<String>) : super() {
        this.context = context
        this.list = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.viewpager2_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = list[position]
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        var textView: TextView
        constructor(itemView: View) : super(itemView) {
            textView = itemView.findViewById<TextView>(R.id.item_text_viewpager2)
        }
    }

}