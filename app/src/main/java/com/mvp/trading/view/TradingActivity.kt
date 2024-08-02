package com.mvp.trading.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bilibili.R
import com.example.bilibili.databinding.ActivityMainTradingBinding
import com.mvp.trading.present.TradingPresent

class TradingActivity : AppCompatActivity(), ITradingView {

    private lateinit var binding : ActivityMainTradingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainTradingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val present = TradingPresent(this)
        binding.result.setOnClickListener {
            present.calculate()
        }

    }

    override fun showResult(result: String) {
        binding.result.text = result
    }
}