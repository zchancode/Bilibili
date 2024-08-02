package com.mvp.trading.model

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface ITradingModel {
    fun calculate(callBack: (String) -> Unit)
}