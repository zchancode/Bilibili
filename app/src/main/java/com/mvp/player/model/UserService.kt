package com.mvp.player.model

import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)


data class RegisterResponse(
    val success: Boolean,
    val message: String
)



data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String
)


data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)


data class GetUserInfoResponse(
    val success: Boolean,
    val message: String,
    val data: UserInfo
)

interface UserService {
    @POST("/api/register")
    fun registerUser(@Body request: RegisterRequest): Observable<RegisterResponse>

    @POST("/api/login")
    fun loginUser(@Body request: LoginRequest): Observable<LoginResponse>

    @POST("/api/getUserInfo")
    fun getUserInfo(): Observable<GetUserInfoResponse>
}