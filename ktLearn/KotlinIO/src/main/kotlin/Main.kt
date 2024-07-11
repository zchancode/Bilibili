package org.example

import java.io.*

fun main() {


    val file = File("file.txt")
    val content = "hello, world!"
    val outputStream = FileOutputStream(file)
    val dataOutputStream = DataOutputStream(outputStream)
    dataOutputStream.writeUTF(content)
    dataOutputStream.writeInt(100000)
    dataOutputStream.close()



    val inputStream = FileInputStream(file)
    val dataInputStream = DataInputStream(inputStream)
    println(dataInputStream.readUTF())
    println(dataInputStream.readInt())
    dataInputStream.close()

}