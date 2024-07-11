package com.example.zchan_webview.webview.webviewprocess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.zchan_webview.R
import com.example.zchan_webview.WebFragment
import com.example.zchan_webview.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_web)
        mBinding.webTitle.setText(intent.getStringExtra("title").toString())
        mBinding.constraintLayout.isVisible = intent.getBooleanExtra("hasTitle", false)
        mBinding.webBack.setOnClickListener({
            finish()
        })


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = WebFragment.newInstance(intent.getStringExtra("url").toString())
        fragmentTransaction.replace(R.id.web_fragment, fragment)
        fragmentTransaction.commit()

    }

    fun updateTitle(title: String?) {
        var title = title ?: "No Title"
        if (title.length > 10) {
            title = title.substring(0, 10) + "..."
        }
        mBinding.webTitle.setText(title)
    }

}