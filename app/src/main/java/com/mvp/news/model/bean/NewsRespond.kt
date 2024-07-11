package com.mvp.news.model.bean

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
data class NewsRespond(
    val code: Int,
    val msg: String,
    val result: Result
)

data class Result(
    val list: List<News>
)

data class News(
    val digest: String,
    val mtime: String,
    val title: String
)