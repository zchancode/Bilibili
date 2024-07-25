package com.view.viewpager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.bilibili.R

class ViewPager2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)
        findViewById<ViewPager2>(R.id.view_pager2).also {
            it.adapter = ViewPager2Adapter(this, listOf("1", "2", "3", "4", "5"))
        }
    }
}