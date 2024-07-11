package com.java.pattern

import kotlin.math.abs

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */

interface Text {
    fun render(): String
}


class PlainText(private val string: String): Text{
    override fun render(): String {
        return string
    }
}

class BoldDecorator(private val text: Text) : Text{
    override fun render(): String {
        return "<b>${text.render()}</b>"
    }
}



class DecoratorPattern {
    companion object {
        fun test() {
            val text = PlainText("Hello, world!")
            println("Text: ${text.render()}")

            val boldText = BoldDecorator(text)
            println("Bold: ${boldText.render()}")

            val bbText = BoldDecorator(BoldDecorator(BoldDecorator(text)))

        }
    }
}