package com.mvp.news.model

import com.mvp.news.model.bean.NewsRespond

interface INewsModel {
    fun getNews(listener: (NewsRespond) -> Unit)
}
