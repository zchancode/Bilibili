package com.retrofit.learn

import com.mvp.news.model.bean.NewsRespond
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

/**
Created by Mr.Chan
Time 2024-07-15
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface NetApi {
    @GET("tx/bulletin?key=150ff6d47b76")
    fun getNews(): Observable<NewsRespond>
}