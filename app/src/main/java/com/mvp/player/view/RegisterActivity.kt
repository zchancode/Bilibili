package com.mvp.player.view

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bilibili.R
import com.example.bilibili.databinding.ActivityRegisterTikBinding
import com.mvp.player.model.RegisterResponse
import com.mvp.player.present.UserPresent
import com.mvp.player.view.base.XBaseActivity

class RegisterActivity : XBaseActivity(), IUserView {

    private lateinit var dialog: ProgressDialog

    private lateinit var binding : ActivityRegisterTikBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterTikBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userPresent = UserPresent(this)
        binding.registerBtn.setOnClickListener {
            userPresent.register(
                binding.username.text.toString(),
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onResult(result: Any) {
        val result = result as RegisterResponse
        if (result.success){
            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun showLoading() {
        dialog = ProgressDialog(this)
        dialog.setTitle("注册中")
        dialog.show()
    }

    override fun hideLoading() {
        dialog.dismiss()
    }
}