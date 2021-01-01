package com.rd.rdcloud2.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * @author D10NG
 * @date on 2020/3/10 5:04 PM
 */

/**
 * 复制
 * @return [T] obj
 */
fun <T> T.copy() : T {
    // 写入字节流
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(this)
    // 读取字节流
    val bais = ByteArrayInputStream(baos.toByteArray())
    val ois = ObjectInputStream(bais)
    return ois.readObject() as T
}

