package com.mvvm.news.viewmodel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.example.bilibili.R
import com.example.bilibili.databinding.MvvmActivityMainBinding

/**
Created by Mr.Chan
Time 2024-06-17
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class MainActivity : AppCompatActivity() {
    val number = MutableLiveData<Int>().apply { value = 0 }
    private lateinit var binding: MvvmActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.mvvm_activity_main)
        binding.viewModel = this
        binding.lifecycleOwner = this
    }

    fun add() {
        val currentNumber = number.value ?: 0
        number.value = currentNumber + 1
    }
}
