package com.mvp.player.model

import android.util.Log
import android.widget.Toast
import com.mvp.player.network.RetrofitManager
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class UserModel: IUserModel {
    override fun login(username: String, password: String, callBack: (LoginResponse) -> Unit) {
        RetrofitManager.getRetrofit().create(UserService::class.java)
            .loginUser(LoginRequest(username, password))
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LoginResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: LoginResponse) {
                    callBack(t)
                    Log.d("UserModel", "onNext $t")
                }

                override fun onError(e: Throwable) {
                    callBack(LoginResponse(false, e.message ?: "error", ""))
                    Log.d("UserModel", "onError $e")
                }

                override fun onComplete() {
                }
            })
    }

    override fun register(
        username: String,
        email: String,
        password: String,
        callBack: (RegisterResponse) -> Unit
    ) {

        Log.d("UserModel", "register")
        RetrofitManager.getRetrofit().create(UserService::class.java)

            .registerUser(RegisterRequest(username, email, password))
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RegisterResponse> {
                override fun onSubscribe(d: Disposable) {
                    Log.d("UserModel", "onSubscribe")
                }

                override fun onNext(t: RegisterResponse) {
                    Log.d("UserModel", "onNext $t")
                    callBack(t)
                }

                override fun onError(e: Throwable) {
                    Log.d("UserModel", "onError $e")
                    callBack(RegisterResponse(false, e.message ?: "error"))
                }

                override fun onComplete() {
                    Log.d("UserModel", "onComplete")
                }
            })
    }

    override fun getUserInfo(token: String, callBack: (GetUserInfoResponse) -> Unit) {
        RetrofitManager.getRetrofit().create(UserService::class.java)
            .getUserInfo(GetUserInfoRequest(token))
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GetUserInfoResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: GetUserInfoResponse) {
                    callBack(t)
                }

                override fun onError(e: Throwable) {
                    callBack(GetUserInfoResponse(false, e.message ?: "error", UserInfo("", "", "", "", "")))
                }

                override fun onComplete() {
                }
            })
    }
}