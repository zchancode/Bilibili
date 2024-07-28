package com.view.viewpager2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.bilibili.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.view.viewpager.BaseFragment


class ViewPager2Activity : AppCompatActivity() {

    class ViewPager2Fragment1 : BaseFragment() {
        override fun initView(view: View) {
        }

        override fun getLayoutId(): Int {
            return R.layout.viewpager_fragment_a
        }
    }

    class ViewPager2Fragment2 : BaseFragment() {
        override fun initView(view: View) {
        }

        override fun getLayoutId(): Int {
            return R.layout.viewpager_fragment_b
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager2)
        findViewById<ViewPager2>(R.id.view_pager2).also {
            it.adapter = ViewPager2Adapter(this, listOf("1", "2", "3", "4", "5"))
        }
        val bottomNavigationView =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation_view)

        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager2_fragment)
        val list = listOf(ViewPager2Fragment1(), ViewPager2Fragment2())
        viewPager2.adapter = ViewPager2FragmentAdapter(supportFragmentManager, lifecycle, list)

        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            tab.setText("Tab " + (position + 1))
        }.attach()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    viewPager2.currentItem = 0
                    tabLayout.getTabAt(0)!!.select()

                }
                R.id.search -> {
                    viewPager2.currentItem = 1
                    tabLayout.getTabAt(1)!!.select()
                }
            }
            true
        }
         viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }
}