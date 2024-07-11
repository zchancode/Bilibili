package com.java.pattern

import java.util.LinkedList

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */


open class ObserverBase {
    private val observers = LinkedList<ObserverBase>()
    fun addObserver(observer: ObserverBase) {
        observers.add(observer)
    }

    fun send(msg: String) {
        for (observer in observers) {
            observer.receive(msg)
        }
    }

    open fun receive(msg: String){}
}

class Student : ObserverBase() {
    override fun receive(msg: String) {
        println("Student receive: $msg")
    }
}

class Teacher : ObserverBase() {

}

object Test {
    fun test() {
        val student1 = Student()
        val student2 = Student()
        val teacher = Teacher()

        teacher.addObserver(student1)
        teacher.addObserver(student2)

        teacher.send("Hello")
    }
}

fun main() {
    Test.test()
}