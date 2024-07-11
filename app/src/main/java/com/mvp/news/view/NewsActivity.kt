package com.mvp.news.view

import android.os.Bundle
import android.view.ViewParent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bilibili.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mvp.news.view.base.BaseActivity


class NewsActivity : BaseActivity() {

    class NewsFragmentPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {

        private val fragmentList = arrayListOf<Fragment>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }
    }


    fun initView() {
        val navButton = findViewById<BottomNavigationView>(R.id.navigation_view)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        NewsFragmentPagerAdapter(supportFragmentManager, 0).also {
            it.addFragment(HomeFragment())
            it.addFragment(MeFragment())
            viewPager.adapter = it

        }
        viewPager.offscreenPageLimit = 2
        /*
        * 这是缓存的意思，设成0还是会缓存一个 设成1 就是 [0] [1] [now] [1] [0]
        * 2就是[1] [1] [now] [1] [1] [0]
        * 2如果超出屏幕 [now] [1] [1] [0] [0] [0]
        *
        * 滑动预加载会卡顿
        */

        /*
        *懒加载
        */

        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        navButton.selectedItemId = R.id.home
                    }

                    1 -> {
                        navButton.selectedItemId = R.id.me
                    }
                }
            }
        })



        navButton.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    viewPager.currentItem = 0
                }

                R.id.me -> {
                    viewPager.currentItem = 1
                }
            }
            true
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_news_activity_news)
        /*
        * setContentView 本质就是调用了LayoutInflater
        * 在viewpage frameLayout listview recyclerview要用
        */
        initView()
    }
}