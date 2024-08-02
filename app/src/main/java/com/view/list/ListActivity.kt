package com.view.list

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.bilibili.R


class ListActivity : AppCompatActivity() {

    val newsData = ArrayList<NewsData>()

    init {
        for (i in 0..20) {
            newsData.add(NewsData("标题$i", "内容$i", "时间$i"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val list = findViewById<RecyclerView>(R.id.recyclerView)
        //linearLayout Manager
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = ListAdapter(this, newsData)
    }
}