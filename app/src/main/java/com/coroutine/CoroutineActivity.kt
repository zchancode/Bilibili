package com.coroutine

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.bilibili.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class CoroutineActivity : AppCompatActivity() {


    data class ApiResponse(val data: List<FundRate>)

    interface ApiService {
        @GET("funding-rate")
        suspend fun listRepos(): ApiResponse
    }

    object NetApi {
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("http://64.176.166.117:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)

        lifecycleScope.launch {
            val apiService = NetApi.getRetrofit().create(ApiService::class.java)
            val dialog = ProgressDialog(this@CoroutineActivity).apply {
                setTitle("正在加载中")
                show()
            }

            try {
                val result = withContext(Dispatchers.IO) {
                    apiService.listRepos()
                }

                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(this@CoroutineActivity, result.toString(), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    Toast.makeText(this@CoroutineActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("TAG", "onCreate: ${e.message}")
                }
            }
        }
    }
}
