package com.mvp.player.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bilibili.R
import com.mvp.player.model.bean.Video
import com.mvp.player.view.XVideoFragment
import java.util.zip.Inflater

class XVideoListAdapter(context: Fragment) : FragmentStateAdapter(context){
    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        return XVideoFragment()
    }

}
