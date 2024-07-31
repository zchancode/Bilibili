package com.mvp.player.view.adapter

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R

class XMeAdapter(private var covers: List<Bitmap>) : RecyclerView.Adapter<XMeAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        var cover: android.widget.ImageView
        init {
            cover = view.findViewById(R.id.cover_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.me_cover_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return covers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cover.setImageBitmap(covers[position])
    }


}
