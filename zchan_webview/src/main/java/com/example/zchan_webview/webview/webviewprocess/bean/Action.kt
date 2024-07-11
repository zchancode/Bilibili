package com.example.zchan_webview.webview.webviewprocess.bean


/**
 * Created by Mr.Chan
 * Time 2024-07-03
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */

data class Action(
    val action: String,
    val params: Map
)

class Map: HashMap<String, String>(){
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("{")
        for (key in keys) {
            sb.append("\"$key\":\"${get(key)}\",")
        }
        sb.deleteCharAt(sb.length - 1)
        sb.append("}")
        return sb.toString()
    }

}