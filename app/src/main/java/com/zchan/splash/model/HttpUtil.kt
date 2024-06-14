package com.zchan.splash.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
Created by Mr.Chan
Time 2024-06-14
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class HttpUtil {
    object Holder{
        val BASE_URL = "https://www.wanandroid.com/"
        val okHttpClient = OkHttpClient.Builder()
        val client = okHttpClient
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                response
            }
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            //设置okhttp请求器
            .client(client)
            //添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            //添加RxJava处理器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    companion object {
        fun getRetrofit(): Retrofit {
            return Holder.retrofit
        }
    }


}