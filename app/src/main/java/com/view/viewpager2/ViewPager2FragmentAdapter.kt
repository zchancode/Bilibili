package com.view.viewpager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
Created by Mr.Chan
Time 2024-07-25
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class ViewPager2FragmentAdapter: FragmentStateAdapter {

    private var list: List<Fragment> = ArrayList()
    constructor(fragmentManager: FragmentManager, lifeCycle: Lifecycle, list: List<Fragment>) : super(fragmentManager, lifeCycle) {
        this.list = list
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}