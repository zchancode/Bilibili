package com.java.pattern

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

interface Work {
    fun work()
}

class Son : Work {
    override fun work() {
        println("Son work")
    }
}

class Father : Work{
    override fun work() {
        println("Father work")
        Son().work()
        println("Father work end")
    }

}

class ProxyPattern {
    companion object {
        fun test() {
            val father = Father()
            father.work()
        }
    }

}




interface ISubject{
    fun request()
    fun send()
}


class RealSubject : ISubject{
    override fun request() {
        println("RealSubject request")
    }

    override fun send() {
        println("RealSubject send")
    }
}

class ProxySubject : ISubject by RealSubject(){
    override fun request() {
        if (true) {
            RealSubject().request()
        }
    }
}

fun main() {
    ProxySubject().request()
    ProxySubject().send()
}