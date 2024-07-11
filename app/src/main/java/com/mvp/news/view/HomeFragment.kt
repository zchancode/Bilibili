package com.mvp.news.view

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.bilibili.R
import com.mvp.news.model.INewsModel
import com.mvp.news.model.NewsModel
import com.mvp.news.model.bean.NewsRespond
import com.mvp.news.view.base.BaseFragment
import com.mvp.news.view.base.LazyBaseFragment

class HomeFragment : LazyBaseFragment() {

    inner class NewsAdapter(val newsRespond: NewsRespond) : BaseAdapter() {

        private val newsList = newsRespond.result.list

        override fun getCount(): Int {
            return newsList.size
        }

        override fun getItem(position: Int): Any {
            return newsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: layoutInflater.inflate(R.layout.mvp_news_item_news, parent, false)
            view.findViewById<TextView>(R.id.newsTitle).text = newsList[position].title
            return view
        }
    }

    private lateinit var textView: AppCompatButton
    var isCreated = false

    override fun canLoad() {
        super.canLoad()
        if (isCreated) //解决崩溃
            textView.setText("加载中")
        //这个函数是在onCreateView之前调用的
        //所以这里的textView是null的

        //解决方法是：强制在onCreateView之后调用


        Log.e("TAG", "HomeFragment 加载数据")
    }

    override fun dontLoad() {
        super.dontLoad()
        Log.e("TAG", "HomeFragment 不加载数据")
    }


    override fun getLayoutId(): Int {
        return R.layout.mvp_news_activity_home
    }

    override fun initView(view: View) {

        view.findViewById<ListView>(R.id.newsListView)
        textView = view.findViewById<AppCompatButton>(R.id.button)
        textView.setOnClickListener {
            val newsModel: INewsModel = NewsModel()
            val newsRespond: (NewsRespond) -> Unit = { res ->
                Toast.makeText(context, res.result.list[0].title, Toast.LENGTH_SHORT).show()
                view.findViewById<ListView>(R.id.newsListView).adapter = NewsAdapter(res)
            }
            newsModel.getNews(newsRespond)
        }

        isCreated = true

        canLoad()
    }

    override fun onResume() {
        super.onResume()
        canLoad()
    }

    override fun onPause() {
        super.onPause()
        dontLoad()
    }

}