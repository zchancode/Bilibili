package com.view.viewpager

import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bilibili.R

class PagerActivity : AppCompatActivity() {

    class FragmentA: BaseFragment() {
        override fun initView(view: View) {

        }

        override fun getLayoutId(): Int {
            return R.layout.viewpager_fragment_a
        }
    }

    class FragmentB: BaseFragment() {
        override fun initView(view: View) {

        }

        override fun getLayoutId(): Int {
            return R.layout.viewpager_fragment_b
        }
    }

    class ViewPagerAdapter(fm: FragmentManager,private val list: List<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Tab$position"
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        val list = listOf(FragmentA(), FragmentB())
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, list)
        tabLayout.setupWithViewPager(viewPager)

    }
}