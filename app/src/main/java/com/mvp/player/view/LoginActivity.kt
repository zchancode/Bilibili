package com.mvp.player.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.bilibili.databinding.ActivityLoginTikBinding
import com.mvp.player.model.LoginResponse
import com.mvp.player.present.IUserPresent
import com.mvp.player.present.UserPresent
import com.mvp.player.view.base.XBaseActivity

class LoginActivity : XBaseActivity(), IUserView {
    //process dialog
    private lateinit var dialog: ProgressDialog

    private lateinit var present: IUserPresent

    private lateinit var binding : ActivityLoginTikBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTikBinding.inflate(layoutInflater)
        setContentView(binding.root)
        present = UserPresent(this)
        binding.loginBtn.setOnClickListener {
            present.login(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }




    }

    override fun onResult(result: Any) {
        val result = result as LoginResponse
        if (result.success){
            Toast.makeText(this, "登录成功 token: ${result.token}", Toast.LENGTH_SHORT).show()
            getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", result.token).apply()
            finishAffinity()
            startActivity(Intent(this, XMainActivity::class.java))

        }

        Toast.makeText(this, "登录失败 ${result.message}", Toast.LENGTH_SHORT).show()


    }

    override fun showLoading() {
        dialog = ProgressDialog(this)
        dialog.setTitle("登录中")
        dialog.show()
    }

    override fun hideLoading() {
        dialog.dismiss()
    }
}