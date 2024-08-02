package com.mvp.player.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mvp.player.view.XHomeFragment
import com.mvp.player.view.XInboxFragment
import com.mvp.player.view.XMeFragment
import com.mvp.player.view.XMainActivity
import com.mvp.player.view.XSearchFragment

class XViewPagerAdapter(xPlayerMainActivity: XMainActivity): FragmentStateAdapter(xPlayerMainActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> XHomeFragment()
            1 -> XSearchFragment()
            2 -> XInboxFragment()
            3 -> XMeFragment()
            else -> XHomeFragment()
        }
    }



}
