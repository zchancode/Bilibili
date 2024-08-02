package com.mvp.player.model

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IUserModel {
    fun login(username: String, password: String, callBack: (LoginResponse) -> Unit)

    fun register(username: String, email: String, password: String, callBack: (RegisterResponse) -> Unit)

    fun getUserInfo(token: String, callBack: (GetUserInfoResponse) -> Unit)
}