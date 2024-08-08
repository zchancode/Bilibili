package com.mvp.player.model

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
Created by Mr.Chan
Time 2024-08-07
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

data class Video(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val url: String,
    val createdAt: String
)

data class SearchResponse(
    val success: Boolean,
    val message: String,
    val data: List<Video>
)

data class SearchRequest(
    val query: String
)



data class GetRandomVideoResponse(
    val success: Boolean,
    val message: String,
    val data: Video
)


interface VideoService {
    @GET("/api/getRandomVideo")
    fun getRandomVideo(): Observable<GetRandomVideoResponse>

    @POST("/api/searchVideo")
    fun searchVideo(@Body searchRequest: SearchRequest): Observable<SearchResponse>

}