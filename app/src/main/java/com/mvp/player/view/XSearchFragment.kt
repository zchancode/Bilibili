package com.mvp.player.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.mvp.player.model.SearchTrending
import com.mvp.player.view.adapter.SearchTrendAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XSearchFragment: XBaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_search
    }

    override fun initView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)

        val trendList = arrayListOf<SearchTrending>()
        trendList.add(SearchTrending("test", 100, "test"))
        trendList.add(SearchTrending("test", 100, "test"))
        trendList.add(SearchTrending("test", 100, "test"))


        recyclerView.adapter = SearchTrendAdapter(requireContext(), trendList)
    }
}