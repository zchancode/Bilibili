package com.mvp.player.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitManager {
    fun getRetrofit(string: String): Retrofit{
        return Retrofit.Builder()
            .baseUrl(string)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
