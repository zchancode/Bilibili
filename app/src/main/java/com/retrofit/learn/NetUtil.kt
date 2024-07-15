package com.retrofit.learn

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
Created by Mr.Chan
Time 2024-07-15
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
object NetUtil {
    fun getNetApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://whyta.cn/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}