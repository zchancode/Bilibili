package com.view.dispatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.bilibili.R

class EventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val view1 = findViewById<com.view.dispatch.custom.CustomViewGroup>(R.id.textView1)
        val view2 = findViewById<com.view.dispatch.custom.CustomView>(R.id.textView2)


    }
}