package com.rd.yunlib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.security.MessageDigest

/**
 * @author D10NG
 * @date on 2020/9/24 11:40 AM
 */

/**
 * 获取设备唯一标记码
 * @receiver Context
 * @return String
 */
fun Context.getAndroidDeviceUniqueId(): String {
    // ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置。
    @SuppressLint("HardwareIds")
    val androidID = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    // +硬件序列号
    @SuppressLint("HardwareIds")
    val id = androidID + Build.SERIAL
    return id.md5()
}

/**
 * md5加密
 * @receiver String
 * @return String
 */
fun String.md5(): String {
    val hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
        var str = Integer.toHexString(b.toInt())
        if (b < 0x10) {
            str = "0$str"
        }
        hex.append(str.substring(str.length -2))
    }
    return hex.toString()
}



