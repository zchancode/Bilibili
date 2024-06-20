package com.mvp.binance.login.present

import android.graphics.Bitmap
import com.mvp.binance.login.model.ILoginModel
import com.mvp.binance.login.model.LoginModel
import com.mvp.binance.login.view.ILoginView

/**
Created by Mr.Chan
Time 2024-06-13
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class LoginPresent(private val iLoginView : ILoginView): ILoginPresent {
    private var iLoginModel: ILoginModel? = null
    init {
        this@LoginPresent.iLoginModel = LoginModel()
    }

    override fun loadImg(path: String): Bitmap? {
        return iLoginModel?.loadImg(path)
    }

    override fun login(username: String, password: String) {
        val callBack: (String) -> Unit = {str ->
            if (str == "登录成功"){
                iLoginView.loginSuccess()
            }else{
                iLoginView.loginFailed()
            }
        }
        iLoginModel?.login(username, password, callBack)
    }

    override fun register(username: String, password: String) {
        val callBack: (String) -> Unit = {str ->
            if (str == "注册成功"){
                iLoginView.loginSuccess()
            }else{
                iLoginView.loginFailed()
            }
        }
        iLoginModel?.register(username, password, callBack)
    }
}