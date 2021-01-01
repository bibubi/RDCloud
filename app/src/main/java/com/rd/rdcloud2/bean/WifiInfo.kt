package com.rd.rdcloud2.bean

/**
 * Wifi信息
 * @author mR2hao
 * @date 2020/12/18
 */
data class WifiInfo constructor(
        var ssid: String = "",
        var bssid: String = "",
        var password: String = "",
        var ip: String = ""
)