package com.zchan.splash.view

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.bilibili.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zchan.splash.view.base.BaseActivity

class SplashActivity : BaseActivity(),BottomNavigationView.OnNavigationItemSelectedListener {

    var fargments = ArrayList<Fragment>()

    fun initViews() {
        val mBottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        mBottomNav.setOnNavigationItemSelectedListener(this)
        fargments.add(HomeFragment())
        fargments.add(ShopFragment())
        fargments.add(MeFragment())
        switchFragment(0)
    }

    fun switchFragment(position: Int) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fargments.get(position)).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initViews()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                switchFragment(0)
                return true
            }

            R.id.shop -> {
                switchFragment(1)
                return true
            }

            R.id.me -> {
                switchFragment(2)
                return true
            }
        }
        return true
    }
}