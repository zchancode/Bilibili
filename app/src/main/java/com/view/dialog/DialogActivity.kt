package com.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.bilibili.R
import com.google.android.material.bottomsheet.BottomSheetDialog


class DialogActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        val showDialog = findViewById<AppCompatButton>(R.id.showDialog)
        showDialog.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            com.example.bilibili.R.id.showDialog -> {
                val dialog = Dialog(this)
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.setContentView(R.layout.activity_dialog)
                dialog.show()
            }
        }
    }
}