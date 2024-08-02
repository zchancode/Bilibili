package com.mvp.trading.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface KlinesApi {
    @GET("fapi/v1/klines")
    fun getKlines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("limit") limit: Int
    ): Observable<String>
}