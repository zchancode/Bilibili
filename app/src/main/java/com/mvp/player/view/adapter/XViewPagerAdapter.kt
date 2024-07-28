package com.mvp.player.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mvp.player.view.XPlayerMainActivity
import com.mvp.player.view.base.XBaseFragment

class XViewPagerAdapter(xPlayerMainActivity: XPlayerMainActivity, fragments: List<XBaseFragment>): FragmentStateAdapter(xPlayerMainActivity) {
    private val fragments = fragments
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
