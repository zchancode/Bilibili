package com.mvp.player.present

import android.content.Context.MODE_PRIVATE
import com.mvp.player.App
import com.mvp.player.model.IUserModel
import com.mvp.player.model.UserModel
import com.mvp.player.view.IUserView

/**
Created by Mr.Chan
Time 2024-08-02
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class UserPresent: IUserPresent {

    private var view: IUserView
    private var model: IUserModel = UserModel()

    constructor(view: IUserView) {
        this.view = view
    }
    override fun login(email: String, password: String) {
        view.showLoading()
        model.login(email, password) {
            view.onResult(it)
            view.hideLoading()
        }
    }

    override fun register(username: String, email: String, password: String) {
        view.showLoading()
        model.register(username, email, password) {
            view.onResult(it)
            view.hideLoading()
        }

    }

    override fun getUserInfo() {
        view.showLoading()
        model.getUserInfo {
            view.onResult(it)
            view.hideLoading()
        }
    }


}