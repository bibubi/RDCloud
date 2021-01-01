package com.rd.rdcloud2.utils

/**
 * byte 转换工具
 *
 * @author D10NG
 * @date on 2019-11-23 16:52
 */

/**
 * 检验校验和
 * @return [Boolean] true:检验成功; false:检验失败;
 */
fun ByteArray.checkEndNum() : Boolean {
    if (this.isEmpty()) return false
    var num = (0).toByte()
    for (i in 0 until this.size -1) {
        num = (num + this[i]).toByte()
    }
    return num == this[this.size - 1]
}

/**
 * 获取校验和
 * @return [Byte] 校验和
 */
fun ByteArray.getChecksum() : Byte {
    var num = (0).toByte()
    for (element in this) {
        num = (num + element).toByte()
    }
    return num
}

/**
 * 添加较验和
 * @return [ByteArray] 增加较验和后的Byte数组
 */
fun ByteArray.addChecksum(): ByteArray {
    val list = this.toMutableList()
    list.add(this.getChecksum())
    return list.toByteArray()
}

/**
 * 将 byte 转为 8位二进制字符串 "00110011"
 * @return [String] 8位二进制字符串
 */
fun Byte.toBinStr() : String {
    val str = Integer.toBinaryString(this.toInt())
    return str.upToNStr(8)
}

/**
 * 将 boolean 数组 转换为 byte
 * @param bools
 * @return [Byte]
 */
fun getByteFromBool(vararg bools: Boolean) : Byte {
    val builder = StringBuilder()
    for (b in bools.iterator()) {
        builder.append(if (b) "1" else "0")
    }
    return builder.toString().binToByte()
}

/**
 * 将 boolean 数组 转换为 byte
 * @return [Byte]
 */
fun List<Boolean>.toByte() : Byte {
    val builder = StringBuilder()
    for (b in this.iterator()) {
        builder.append(if (b) "1" else "0")
    }
    return builder.toString().binToByte()
}

/**
 * 将二进制字符串 "00110011" 转为 byte
 * @return [Byte]
 */
fun String.binToByte() : Byte {
    val value = Integer.valueOf(this, 2)
    return value.toByte()
}

/**
 * 从16进制字符串中获取Byte数组
 * @return [ByteArray]
 */
fun String.hexStringToByteArray(): ByteArray {
    if (this.isEmpty()) return byteArrayOf()
    val list = mutableListOf<Byte>()
    var str = this.copy()
    while (str.isNotEmpty()) {
        if (str.length < 2) {
            str = str.upToNStrInBack(2)
        }
        list.add(str.substring(0, 2).toInt(16).toByte())
        str = if (str.length > 2) str.substring(2) else ""
    }
    return list.toByteArray()
}

/**
 * 将Byte数组转为16进制字符串
 * @return [String]
 */
fun ByteArray.byteArrayToHexStr(): String {
    val builder = StringBuilder()
    for (b in this) {
        builder.append((b.toInt() and 0xFF).toString(16).upToNStr(2))
    }
    return builder.toString()
}

/**
 * 将整型转换成Byte数组
 * @receiver Int
 * @param size Int
 * @return ByteArray
 */
fun Long.toByteArray(size: Int): ByteArray {
    var str = this.toString(16)
    if (str.length % 2 > 0) str = "0$str"
    val byteArray = str.hexStringToByteArray()
    val byteList = mutableListOf<Byte>()
    when {
        byteArray.size < size -> {
            for (i in byteArray.size until size) {
                byteList.add(0.toByte())
            }
            byteList.addAll(byteArray.toMutableList())
        }
        else -> {
            byteList.addAll(byteArray.toMutableList().subList(0, size))
        }
    }
    return byteList.toByteArray()
}

/**
 * 将两个字节的byte数组转换成有符号整型
 * @param byte1 高位
 * @param byte2 低位
 */
fun convertSignInt(byte1: Byte, byte2: Byte): Int =
    (byte1.toInt() shl 8) or (byte2.toInt() and 0xFF)

/**
 * 将两个字节的byte数组转换成无符号整型
 * @param byte1 高位
 * @param byte2 低位
 */
fun convertUnSignInt(byte1: Byte, byte2: Byte): Int =
    (byte1.toInt() and 0xFF) shl 8 or (byte2.toInt() and 0xFF)

/**
 * 获取整型数据的 高位 byte
 * @param value 整型数据
 */
fun convertUnSignByteHeight(value: Int): Byte =
    value.ushr(8).toByte()

/**
 * 获取整型数据的 低位 byte
 * @param value 整型数据
 */
fun convertUnSignByteLow(value: Int): Byte =
    (value and 0xff).toByte()