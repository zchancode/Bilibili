package com.cookie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.bilibili.R

class CookieActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cookie)
        findViewById<Button>(R.id.saveSharePreference).also {
            it.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.saveSharePreference -> {
                val share = getSharedPreferences("myShare", MODE_PRIVATE)
                val editor = share.edit().putString("token", "x-sqe13sqrfq3").also {
                    it.apply()
                }

                editor.putString("token", "x").apply()
                Toast.makeText(this, share.getString("token", ""), Toast.LENGTH_SHORT).show()


            }
        }
    }
}