package com.mvp.player.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bilibili.R
import com.example.bilibili.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.findBack.setOnClickListener {
            finish()
        }

        binding.scanBtn.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
    }
}