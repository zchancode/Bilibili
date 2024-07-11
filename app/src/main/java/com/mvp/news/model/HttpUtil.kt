package com.mvp.news.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class HttpUtil {
    object Holder {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://whyta.cn/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        fun getRetrofit(): Retrofit {
            return Holder.retrofit
        }
    }
}