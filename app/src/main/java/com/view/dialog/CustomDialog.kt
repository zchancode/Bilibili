package com.view.dialog

import android.app.Dialog
import android.content.Context

/**
Created by Mr.Chan
Time 2024-07-18
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class CustomDialog: Dialog {
    constructor(context: Context): super(context) {
        setContentView(com.example.bilibili.R.layout.activity_dialog)
        window?.setGravity(android.view.Gravity.BOTTOM)
        window?.setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.WRAP_CONTENT)
    }
}