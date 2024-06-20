package com.mvp.news.model.api

import com.mvp.news.model.bean.NewsRespond
import io.reactivex.Observable
import retrofit2.http.GET

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface BulletinApi {

    @GET("tx/bulletin?key=150ff6d47b76")
    fun getNews(): Observable<NewsRespond>

}