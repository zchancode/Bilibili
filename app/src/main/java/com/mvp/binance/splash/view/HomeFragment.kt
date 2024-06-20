package com.mvp.binance.splash.view

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.bilibili.R

import com.mvp.binance.splash.view.base.BaseFragment

class HomeFragment : BaseFragment() {

    class Person {
        val name: String = "zhangsan"
        var age: Int = 20
    }
    //age可以被set和get 但是name不能被set可以被get

    val addFun: (Int, Int) -> Int = { a, b -> a + b }
    //lambda表达式

    //嵌套函数
    fun add(a: Int, b: Int): Int {
        val add2: (Int, Int) -> Int = { a, b -> a + b }
        return add2(a, b)
    }

    val str = """
        AAAAA
        \n
        BBBBB
        CCCCC
        DDDDD
    """.trimIndent()

    //Null机制
    val info: String? = null

    fun funT(): Int? {
        return null
    }

    fun main() {
        println(info?.length)
        println(info!!.length)
        println(funT()?.inv())
    }


    //区间
    fun range() {
        for (i in 1..10) {
            println(i)
        }
        for (i in 1 until 10) {
            println(i)
        }
        for (i in 10 downTo 1) {
            println(i)
        }
        for (i in 1..10 step 2) {
            println(i)
        }
        val i = 10
        if (i in 1..10) {
            println("i is in the range")
        }
    }


    //比较和数组
    fun compare() {
        val name1 = "zhangsan"
        val name2 = "zhangsan"
        println(name1 == name2)//比较值
        println(name1.equals(name2)) //java中的equals
        println(name1 === name2) //比较地址

        when (20) {
            in 1..10 -> println("1-10")
            in 11..20 -> println("11-20")
            else -> println("other")
        }


        val res = when (20) {
            in 1..10 -> "1-10"
            in 11..20 -> "11-20"
            else -> "other"
        }


        val array = arrayOf(1, 2, 3)
        fun add(): (Int) -> Int {
            return { a -> 2 * a }
        }

        val array2 = Array(3, add())
        val size = if (array.isNotEmpty()) {
            println("array is not empty")
            array.size
        } else {
            println("array is empty")
            0
        }
    }

    //循环和标签
    fun loop() {

        for (i in 1..10) {
            if (i == 5) {
                break
            }
            println(i)
        }

        tag@ for (i in 1..10) {
            for (j in 1..10) {
                if (j == 5) {
                    break@tag
                }
                println(j)
            }
        }

        val array = arrayOf(1, 2, 3)
        val eachFun: (Int) -> Unit = { a -> println(a) }
        array.forEach(eachFun)

    }

    //类与对象
    open class Student(id: Int) {
        constructor(id: Int, name: String) : this(id) {
            println("id: $id, name: $name")
        }

        constructor() : this(1)
    }

    val student = Student() //次构造
    val student2 = Student(2)//主构造
    val student3 = Student(3, "zhangsan")


    class BadStudent(id: Int) : Student(id) {
        constructor(id: Int, name: String) : this(id) {
            println("id: $id, name: $name")
        }

        constructor() : this(1)

        lateinit var name: String
        val age: Int = 20
    }

    interface IFruit {
        fun eat()
    }

    abstract class Fruit() : IFruit {
        abstract fun name()

    }

    class Apple : Fruit() {
        override fun name() {
            println("Apple")
        }

        override fun eat() {
            println("eat Apple")
        }
    }

    fun TestFun() {
        val apple: IFruit = Apple()
        apple.eat()
    }


    //数据类
    data class User(val name: String, val age: Int)

    //get set toString equals hashCode copy
    fun T() {
        val user = User("zhangsan", 20)
        val (_, age) = user.copy()
        println(age)
    }

    //单例
    object Engine {
        fun start() {
            println("start")
        }
    }

    class Engine2 {
        object Holder {
            val instance = Engine2()
        }

        companion object {
            fun getInstance(): Engine2 = Holder.instance
        }

        fun start() {
            println("start")
        }
    }

    class Engine3 {
        companion object {
            private var instance: Engine3? = null
            fun getInstance(): Engine3 {
                if (instance == null) {
                    instance = Engine3()
                }
                return instance!!
            }
        }

        fun start() {
            println("start")
        }
    }

    fun T2() {
        Engine.start()
        Engine2.getInstance().start()
        Engine3.getInstance().start()
    }

    //内部类与嵌套类
    class Test {
        val name = "zhangsan"

        inner class SubTest {
            //内部类
            fun show() = name
        }

        class SubTest2 { //嵌套类
            fun show() = "name"
        }
    }


    //泛型
    class Box<T>(t: T) {
        var value = t
    }

    fun <T> add(a: T, b: T): T {
        return a
    }


    fun T3() {
        val box: Box<Int> = Box(1)
        val box2: Box<String> = Box("zhangsan")
        println(box.value)
        println(box2.value)
        println(add(1, 2))
        println(add("zhangsan", "lisi"))
    }


    class TransactionItemData {
        var name: String = ""

        constructor(name: String) {
            this.name = name
        }

    }

    class TransactionAdapter(val data: List<TransactionItemData>) : BaseAdapter() {

        class ViewHolder {
            lateinit var tvTransactionName: TextView
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val viewHolder: ViewHolder
            if (convertView == null) {
                view = View.inflate(parent?.context, R.layout.transactions_item, null)
                viewHolder = ViewHolder()
                viewHolder.tvTransactionName = view.findViewById(R.id.tvTransactionName)
                view.tag = viewHolder
            } else {
                view = convertView
                viewHolder = view.tag as ViewHolder
            }
            viewHolder.tvTransactionName.text = data[position].name
            return view
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.mvp_binance_fragment_home
    }

    override fun initView(view: View) {
        val data = ArrayList<TransactionItemData>().also {
            it.add(TransactionItemData("Nike Super Store"))
            it.add(TransactionItemData("Puma Store"))
            it.add(TransactionItemData("Transaction 3"))
        }
        view.findViewById<ListView>(R.id.transitionList).also {
            it.adapter = TransactionAdapter(data)
            it.divider = null
        }

    }

}
