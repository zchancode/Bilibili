package com.mvp.player.view

import android.os.Bundle
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
        val bottomNavigationView = binding.bottomNavigationView
        val fragments = listOf(
            XHomeFragment(),
            XSearchFragment(),
            XSettingFragment()
        )

        val adapter = XViewPagerAdapter(this, fragments)
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })


        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    viewPager2.currentItem = 0
                }
                R.id.search -> {
                    viewPager2.currentItem = 1
                }
                R.id.setting -> {
                    viewPager2.currentItem = 2
                }
            }
            true
        }

    }
}