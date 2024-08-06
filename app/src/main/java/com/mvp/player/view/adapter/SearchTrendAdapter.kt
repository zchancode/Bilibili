package com.mvp.player.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bilibili.R
import com.mvp.player.model.SearchTrending

class SearchTrendAdapter : RecyclerView.Adapter<SearchTrendAdapter.ViewHolder> {

    private var context: Context
    private var trendModel: List<SearchTrending>

    constructor(context: Context,trendModel : List<SearchTrending>) : super() {
        this.context = context
        this.trendModel = trendModel
    }

    class ViewHolder: RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView)

        val title = itemView.findViewById<TextView>(R.id.trend_title)
        val hot = itemView.findViewById<TextView>(R.id.trend_hot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.x_player_item_search_trend, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trendModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = trendModel[position].name
        holder.hot.text = trendModel[position].viewTimes.toString()
    }

}
