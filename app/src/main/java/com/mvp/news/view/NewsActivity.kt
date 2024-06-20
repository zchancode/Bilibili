package com.mvp.news.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import com.example.bilibili.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mvp.news.view.base.BaseActivity

class NewsActivity : BaseActivity() {
    val fragments = arrayOf(HomeFragment(), MeFragment())
    fun initView() {
        findViewById<FragmentContainerView>(R.id.fragment_container)
        findViewById<BottomNavigationView>(R.id.navigation_view).also {
            it.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        selectFragment(0)
                        Toast.makeText(this, "home", Toast.LENGTH_SHORT).show()
                    }

                    R.id.me -> {
                        selectFragment(1)
                        Toast.makeText(this, "me", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
    }

    fun selectFragment(position: Int) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragments[position]).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_news_activity_news)
        /*
        * setContentView 本质就是调用了LayoutInflater
        * 在viewpage frameLayout listview recyclerview要用
        *
        */



        initView()
        selectFragment(0)
    }
}