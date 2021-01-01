package com.rd.rdcloud2.utils

import com.blankj.utilcode.util.LogUtils
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 字符串工具
 *
 * @author D10NG
 * @date on 2019-11-22 12:27
 */

/**
 * 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。
 * 但有时需要以字节单位获得字符串的长度。例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。
 * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。
 * 因此，可以编写如下的方法来获得以字节为单位的字符串长度。
 * @return [Int] 字符总数
 */
fun String.getWordCount(): Int {
    var length = 0
    for (i in this.indices) {
        val ascii = Character.codePointAt(this, i)
        if (ascii in 0..255) length++
        else length += 2
    }
    return length
}

/**
 * 补全length位，不够的在前面加0
 * @param length 长度
 * @param paddingString 填充内容 缺省值"0"
 * @return [String] 新字符串
 */
fun String.upToNStr(length: Int, paddingString: String = "0"): String {
    var result = StringBuilder()
    if (this.length < length) {
        for (i in 0 until length - this.length) {
            result.append(paddingString)
        }
        result.append(this)
    } else {
        result = StringBuilder(this)
    }
    return result.toString().substring(result.length - length)
}

/**
 * 补全length位，不够的在后面加0
 * @param length 长度
 * @param paddingString 填充内容 缺省值"0"
 * @return [String] 新字符串
 */
fun String.upToNStrInBack(length: Int, paddingString: String = "0"): String {
    var result = StringBuilder()
    if (this.length < length) {
        result.append(this)
        for (i in 0 until length - this.length) {
            result.append(paddingString)
        }
    } else {
        result = StringBuilder(this)
    }
    return result.toString()
}

/**
 * 将 unicode 转换成字符串，不带"\\u"
 * @return [String] 新字符串
 */
fun String.unicodeNoPrefixToUtf8(): String {
    val builder = StringBuilder()
    val offset = this.length % 4
    var value = this
    if (offset != 0) {
        value = upToNStrInBack(this.length + (4 - offset))
    }
    for (i in value.indices step 4) {
        val end = i + 4
        if (this.length >= end) {
            val item = value.substring(i, end)
            val data = Integer.parseInt(item, 16)
            if (data != 0) builder.append(data.toChar())
        }
    }
    return builder.toString()
}

/**
 * 将 unicode 转换成字符串，带"\\u"
 * @return [String] 新字符串
 */
fun String.unicodeToUtf8(): String {
    return this.replace("\\u", "").unicodeNoPrefixToUtf8()
}

/**
 * 将字符串转换成 unicode，不带"\\u"
 * @return [String] 新字符串
 */
fun String.utf8ToUnicodeNoPrefix(): String {
    val builder = StringBuilder()
    for (c in this.iterator()) {
        val item = Integer.toHexString(c.toInt())
        builder.append(item.upToNStr(4))
    }
    return builder.toString()
}

/**
 * 将字符串转换成十六进制字符串
 * @receiver String
 * @return String
 */
fun String.asciiToHexString(): String {
    val builder = StringBuilder()
    for (c in this.iterator()) {
        val item = Integer.toHexString(c.toInt())
        builder.append(item)
    }
    return builder.toString()
}

fun String.utf8ToGBK(): String {

    val sb = java.lang.StringBuilder()
    val split: List<String> = this.split(" ")
    for (str in split) {
        LogUtils.e(str.toInt())
//        val i = str.toInt(16)
//        sb.append(i.toChar())
    }
    return sb.toString()

}


/**
 * 将字符串转换成 unicode，带"\\u"
 * @return [String] 新字符串
 */
fun String.utf8ToUnicode(): String {
    val builder = StringBuilder()
    for (c in this.iterator()) {
        val item = Integer.toHexString(c.toInt())
        builder.append("\\u").append(item.upToNStr(4))
    }
    return builder.toString()
}

/**
 * 将byte数组转换成16进制字符串
 * @param space 每个byte中间是否需要空格
 * @return [String] 新字符串
 */
fun ByteArray.toHexStr(space: Boolean): String {
    val builder = StringBuilder("")
    return if (this.isNotEmpty()) {
        for (i in this.indices) {
            val value = this[i].toInt()
            var hex = Integer.toHexString(value)
            if (hex.length < 2) {
                builder.append("0")
            } else if (hex.length > 2) {
                hex = hex.substring(hex.length - 2)
            }
            builder.append(hex)
            if (space) builder.append(" ")
        }
        builder.toString()
    } else {
        ""
    }
}

/**
 * 16进制字符串转换成2进制字符串
 * @return [String] 新字符串
 */
fun String.toBinStr(): String {
    val list = this.toList()
    val builder = StringBuilder()
    for (hex in list.iterator()) {
        val intValue = "$hex".toInt(16)
        builder.append(intValue.toString(2).upToNStr(4))
    }
    return builder.toString()
}

/**
 * 2进制字符串转换成16进制字符串
 * @return [String] 新字符串
 */
fun String.toHexStr(): String {
    val intStr = this.toInt(2)
    return intStr.toString(16)
}

/**
 * 字符串反向排序
 * @return [String] 新字符串
 */
fun String.reversed(): String {
    val list = this.toList()
    val builder = StringBuilder()
    for (i in list.iterator()) {
        builder.insert(0, i)
    }
    return builder.toString()
}

/**
 * 从16进制字符串中得到二进制字符1的位置列表
 * @return [List] 位置列表
 */
fun String.getBinIndexList(): List<Int> {
    val byteStr = this.toBinStr()
    // 进行反向排序
    var byteSrtReverse = byteStr.reversed()
    val indexList = mutableListOf<Int>()
    while (byteSrtReverse.contains('1')) {
        indexList.add(byteSrtReverse.indexOf('1'))
        byteSrtReverse = byteSrtReverse.replaceFirst('1', '0')
    }
    return indexList
}

/**
 * 从二进制中1的位置列表得到16进制字符串
 * @return [String] 新字符串
 */
fun List<Int>.binaryIndexListToHexStr(): String {
    var max = 0
    for (i in this) {
        max = max.coerceAtLeast(i)
    }
    val binBuilder = StringBuilder()
    for (i in 0..max) {
        binBuilder.insert(0, if (this.contains(i)) '1' else '0')
    }
    var binStr = binBuilder.toString()
    val hexBuilder = StringBuilder()
    while (binStr.isNotEmpty()) {
        if (binStr.length < 4) {
            binStr = binStr.upToNStr(4)
        }
        val intValue = binStr.substring(binStr.length - 4).toInt(2)
        hexBuilder.insert(0, intValue.toString(16))
        binStr = binStr.substring(0, binStr.length - 4)
    }
    return hexBuilder.toString()
}

/**
 * ASCII码hex字符串转String明文
 * @return [String] 新字符串
 */
fun String.asciiHexToStr(): String {
    val builder = StringBuilder()
    var i = 0
    while (i < this.length - 1) {
        val h = this.substring(i, i + 2)
        val decimal = h.toInt(16)
        builder.append(decimal.toChar())
        i += 2
    }
    return builder.toString()
}

/**
 * 为字符串添加空格
 * @date 2020/12/23
 * @param jump Int 多少位空一格 默认值:2
 * @return 返回添加完空格的字符串
 */
fun String.addSpace(jump: Int = 2): String {
    val regex = "(.{$jump})"
    return this.replace(regex.toRegex(), "$1 ")
}

/**
 * 判断字符串中是否包含中文
 * @param str
 * 待校验字符串
 * @return 是否为中文
 * @warn 不能校验是否为中文标点符号
 */
fun String.isContainChinese(): Boolean {
    val p: Pattern = Pattern.compile("[\u4e00-\u9fa5]")
    val m: Matcher = p.matcher(this)
    return m.find()
}