package com.mvp.player.view

import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.bilibili.R
import com.example.bilibili.databinding.XactivityPlayerMainBinding
import com.mvp.player.view.adapter.XViewPagerAdapter
import com.mvp.player.view.base.XBaseActivity

class XPlayerMainActivity : XBaseActivity() {


    private lateinit var binding: XactivityPlayerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = XactivityPlayerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

    }

    fun initView() {
        val viewPager2 = binding.viewPager
        val bottomNavigationView = binding.navBar
        val fragments = listOf(
            XHomeFragment(),
            XSearchFragment(),
            XInboxFragment(),
            XMeFragment()
        )

        val adapter = XViewPagerAdapter(this, fragments)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

            }
        })

        val buttonClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.home -> {
                    viewPager2.currentItem = 0
                }

                R.id.search -> {
                    viewPager2.currentItem = 1
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
        binding.add.setOnClickListener(buttonClickListener)
        binding.inbox.setOnClickListener(buttonClickListener)
        binding.me.setOnClickListener(buttonClickListener)

    }
}