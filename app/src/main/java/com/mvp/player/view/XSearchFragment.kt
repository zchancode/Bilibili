package com.mvp.player.view

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.example.playercore.Log
import com.mvp.player.model.SearchResponse
import com.mvp.player.model.SearchTrending
import com.mvp.player.model.Video
import com.mvp.player.present.VideoPresent
import com.mvp.player.view.adapter.SearchResultAdapter
import com.mvp.player.view.adapter.SearchTrendAdapter
import com.mvp.player.view.base.XBaseFragment

/**
Created by Mr.Chan
Time 2024-07-27
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class XSearchFragment : XBaseFragment(), IVideoView {
    override fun getLayoutId(): Int {
        return R.layout.x_player_fragment_search
    }

    override fun initView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)

        val trendList = arrayListOf<SearchTrending>()
        trendList.add(SearchTrending("test", 100, "test"))
        trendList.add(SearchTrending("test", 100, "test"))
        trendList.add(SearchTrending("test", 100, "test"))


        recyclerView.adapter = SearchTrendAdapter(requireContext(), trendList)


        val searchResultLayout = view.findViewById<View>(R.id.searchResultLayout)
        val searchResultView = view.findViewById<RecyclerView>(R.id.recycler_view_search)
        searchResultView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(),2)
        val searchResultList = arrayListOf<String>()
        searchResultList.add("test")
        searchResultList.add("test")
        searchResultList.add("test")
        searchResultView.adapter = SearchResultAdapter(searchResultList)


        val scanBtn = view.findViewById<TextView>(R.id.scanBtn)



        val videoPresent = VideoPresent(this)

        val searchBox = view.findViewById<EditText>(R.id.searchBox)
        searchBox.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val query = searchBox.text.toString()
                videoPresent.searchVideo(query)
                searchResultLayout.visibility = View.VISIBLE
                scanBtn.background = resources.getDrawable(R.drawable.baseline_clear_24)
                scanBtn.setOnClickListener{
                    searchResultLayout.visibility = View.GONE
                    scanBtn.text = ""
                    scanBtn.background = resources.getDrawable(R.drawable.search_ico)
                }

                true
            } else {
                false
            }
        }

        //back listener
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchResultLayout.visibility == View.VISIBLE) {
                    searchResultLayout.visibility = View.GONE
                    scanBtn.text = ""
                    scanBtn.background = resources.getDrawable(R.drawable.search_ico)
                }
            }
        })
    }

    override fun onResult(result: Any) {
        val result = result as SearchResponse
        Toast.makeText(requireContext(), result.data.size.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

}