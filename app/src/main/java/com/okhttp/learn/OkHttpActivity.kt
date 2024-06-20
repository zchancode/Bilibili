package com.okhttp.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bilibili.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class OkHttpActivity : AppCompatActivity() {
    fun okHttpGetAsyn() {
        val okHttpClient = OkHttpClient()
        /*如果要对okhttp进行自己的配置就使用构建*/
        val request = okhttp3.Request.Builder().url("https://whyta.cn/api/tx/bulletin?key=150ff6d47b76").build()
        //把请求给客户端
        val call = okHttpClient.newCall(request) //得到一个随时可以请求的call对象

        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                println("请求失败")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body
                val string = body?.string()
                println(string)
            }
        })
        /*
        * 我们看看enqueue方法的源码
        * 1.首先会判断是否已经执行过了，如果执行过了就会抛出异常
        * 2.然后会client.dispatcher().enqueue(new AsyncCall(responseCallback));
        *   真正工作就是这个方法会把请求放到队列中
        *   dispatcher得到okhttp里的分发器，分发器可以自己配置，可以设置最大请求数，最大并发数等
        *   自己配置的话就是okhttp的构建的时候配置
        *   OkHttpClient.Builder().dispatcher(Dispatcher())
        *   一般不会自己配置，okhttp默认的分发器是Dispatcher()
        *
        *
        * 3.我们去看分发器dispatcher的enqueue方法
        *   把回调包装成一个AsyncCall对象，然后放到队列中
        *   对于异步任务有两个队列，一个是ready等待执行 一个是running马上执行
        *
        *   enqueue可能把任务放到ready队列，也可能放到running队列，什么时候放到running队列呢这是个问题
        *   参考最大并发数默认是64 小于64就放到running队列 还有就是正在执行的任务的相同host不超过5个
        *
        *   add到running队列马上执行
        *       executeService.execute(asyncCall) 线程池跑任务，任务本质是Runnable 继承NamedRunnable
        *
        *   add到ready队列等待执行
        *
        * 4.那么ready队列的任务什么时候执行呢？
        *   当running队列的任务执行完毕会调用分发器里的finish方法
        *   finish先remove掉running队列的任务，然后会从ready队列取出一个任务执行
        *
        *
        *
        */
    }

    fun okHttpGet() {
        val okHttpClient = OkHttpClient()
        /*如果要对okhttp进行自己的配置就使用构建*/
        val request = okhttp3.Request.Builder().url("https://whyta.cn/api/tx/bulletin?key=150ff6d47b76").build()
        //把请求给客户端
        val call = okHttpClient.newCall(request) //得到一个随时可以请求的call对象
        val response = call.execute() //同步请求，会阻塞线程
        //call.execute() //同步请求，会阻塞线程
        //java.lang.IllegalStateException: Already Executed
        val body = response.body
        val string = body?.string()
        println(string)
    }

    fun okHttpPost() {
        val okHttpClient = OkHttpClient()
        val requestBody = okhttp3.FormBody.Builder().add("key", "value").build()
        val request = okhttp3.Request.Builder().url("https://www.baidu.com").post(requestBody).build()
        val call = okHttpClient.newCall(request)
        val response = call.execute()
        val body = response.body
        val string = body?.string()
        println(string)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ok_http)
        GlobalScope.launch {
            okHttpGet()
        }
    }
}