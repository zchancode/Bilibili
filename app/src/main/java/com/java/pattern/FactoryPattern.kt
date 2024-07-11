package com.java.pattern

/**
Created by Mr.Chan
Time 2024-06-28
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class SplitterFactory {
    fun createSplitter(type: String): ISplitter? {
        return when (type) {
            "video" -> VideoSplitter()
            "audio" -> AudioSplitter()
            else -> null
        }
    }
}

open class ISplitter {
    open fun split() {}
}

class VideoSplitter : ISplitter() {
    override fun split() {
        println("VideoSplitter split")
    }
}

class AudioSplitter : ISplitter() {
    override fun split() {
        println("AudioSplitter split")
    }
}


/*
 *   TODO 工厂模式
 *   Iterface object = new XXX()
 *   虽然用了接口，但这里依赖了一个具体类
 *
 */

fun main() {
    val factory = SplitterFactory()
    val videoSplitter = factory.createSplitter("video")
    videoSplitter?.split()
    val audioSplitter = factory.createSplitter("audio")
    audioSplitter?.split()

}
