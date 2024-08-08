package com.mvp.player.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.viewpager2.widget.ViewPager2
import com.example.bilibili.R
import com.example.bilibili.databinding.XactivityPlayerMainBinding
import com.mvp.player.view.adapter.XViewPagerAdapter
import com.mvp.player.view.base.XBaseActivity

class XMainActivity : XBaseActivity() {

    private lateinit var binding: XactivityPlayerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = XactivityPlayerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        val viewPager2 = binding.viewPager


        val adapter = XViewPagerAdapter(this)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT



        val buttonClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.home -> {
                    viewPager2.currentItem = 0


                }

                R.id.search -> {
                    viewPager2.currentItem = 1

                }

                R.id.cameraxShow -> {
                    startActivity(Intent(this, CameraXActivity::class.java))
                }

                R.id.inbox -> {
                    viewPager2.currentItem = 2
                }

                R.id.me -> {
                    viewPager2.currentItem = 3
                }

            }
        }

        binding.home.setOnClickListener(buttonClickListener)
        binding.search.setOnClickListener(buttonClickListener)
        binding.cameraxShow.setOnClickListener(buttonClickListener)
        binding.inbox.setOnClickListener(buttonClickListener)
        binding.me.setOnClickListener(buttonClickListener)

    }
}