package com.view.fragment

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.example.bilibili.R

class FragmentActivity : AppCompatActivity(), View.OnClickListener {
    class FragmentA() : BaseFragment() {
        private lateinit var fragmentAListener: FragmentAListener

        interface FragmentAListener {
            fun onFragmentA(string: String)
        }

        private lateinit var context: Context
        private lateinit var activityFun: (String) -> Unit

        constructor(context: Context, function: (String) -> Unit,fragmentAListener: FragmentAListener) : this() {
            this.context = context
            this.activityFun = function
            this.fragmentAListener = fragmentAListener
        }


        override fun getLayoutId(): Int {
            return R.layout.view_fragment_a
        }

        override fun initView(view: View) {
            val button = view.findViewById<TextView>(R.id.fragmentATextView)
            button.setOnClickListener {
                if (context is FragmentActivity) {
                    (context as FragmentActivity).showToast("FragmentA")
                }
                activityFun("FragmentA from (String) -> Unit")
                fragmentAListener.onFragmentA("FragmentA from FragmentAListener")


            }
        }
    }

    class FragmentB() : BaseFragment() {

        private lateinit var context: Context

        private lateinit var activityFun: (String) -> Unit
        constructor(context: Context, function: (String) -> Unit) : this() {
            this.context = context
            this.activityFun = function
        }

        override fun getLayoutId(): Int {
            return R.layout.view_fragment_b
        }

        override fun initView(view: View) {
            val button = view.findViewById<TextView>(R.id.fragmentBTextView)
            button.setOnClickListener {
                if (context is FragmentActivity) {
                    (context as FragmentActivity).showToast("FragmentB")
                }
                activityFun("FragmentB from (String) -> Unit")
            }
        }

    }


    private lateinit var fragmentContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        fragmentContainer = findViewById<FragmentContainerView>(R.id.fragmentContainer)
        findViewById<AppCompatButton>(R.id.changeFragment).also {
            it.setOnClickListener(this)
        }

        val fragmentA = FragmentA(this, ::showToast, object : FragmentA.FragmentAListener {
            override fun onFragmentA(string: String) {
                showToast(string)
            }
        })

        val fragmentB = FragmentB(this, ::showToast)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainer, fragmentA, "FragmentA")
        transaction.add(R.id.fragmentContainer, fragmentB, "FragmentB")
        transaction.commitNow()


        val showTran = supportFragmentManager.beginTransaction()
        showTran.show(fragmentA)
        showTran.hide(fragmentB)
        showTran.commitNow()


    }

    fun showToast(string: String) {
        Toast.makeText(this, "Hello $string", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        changeFragment()
    }

    fun changeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.commit()
    }
}