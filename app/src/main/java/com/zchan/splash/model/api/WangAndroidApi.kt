package com.zchan.splash.model.api

import com.zchan.splash.model.bean.ProjectBean
import com.zchan.splash.model.bean.ProjectItem
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface WangAndroidApi {
    @GET("project/tree/json")
    fun getProject(): Observable<ProjectBean>

    @GET("project/list/{pageIndex}/json")
    fun getProjectList(@Path("pageIndex") pageIndex: Int, @Query("cid") cid: Int): Observable<ProjectItem>
}