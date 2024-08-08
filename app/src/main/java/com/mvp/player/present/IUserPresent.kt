package com.mvp.player.present

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
interface IUserPresent {
    fun login(email: String, password: String)

    fun register(username: String, email: String, password: String)

    fun getUserInfo()

}