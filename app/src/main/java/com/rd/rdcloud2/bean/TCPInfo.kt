package com.rd.rdcloud2.bean

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rd.rdcloud2.utils.byteArrayToHexStr
import com.rd.rdcloud2.utils.toHexStr
import java.io.Serializable

/**
 * TCP数据包
 * @author mR2hao
 * @date 2020/12/22
 */

data class TCPInfo(
        /** 字节数据 */
        var byteData: ByteArray = byteArrayOf(),
        /** 时间 */
        var time: Long = 0L,
        /** 地址 */
        var ipAddress: String = "",
        /** 端口 */
        var port: Int = 0

) : Serializable {

    companion object {
        /************************************APP命令类型***************************************/
        /** APP发送 命令类型 版本协商 */
        const val APP_TYPE_VERSION = "01"

        /** APP发送 命令类型 密钥协商 */
        const val APP_TYPE_KEY = "02"

        /** APP发送 命令类型 配网信息 */
        const val APP_TYPE_WIFI_INFO = "02"

        /** APP发送 命令类型 心跳包 */
        const val APP_TYPE_HEARTBEAT = "0F"

        /************************************设备命令类型*************************************/
        /** 设备回复 命令类型 版本协商 */
        const val DEVICE_TYPE__VERSION = "81"

        /** 设备回复 命令类型 固定密钥配网信息 */
        const val DEVICE_TYPE_KEY = "82"

        /** 设备回复 命令类型 配网信息 */
        const val DEVICE_TYPE_WIFI_INFO = "02"

        /** 设备回复 命令类型 心跳包 */
        const val DEVICE_TYPE_HEARTBEAT = "8F"

        /************************************APP命令号***************************************/
        /** APP发送 命令号 版本协商 */
        const val CODE_SEND_CHECK_VERSION = "01"

        /** APP发送 命令号 版本采纳协商 */
        const val CODE_SEND_ACCEPT_VERSION = "02"

        /** APP发送 命令号 配网信息 (固定密钥)*/
        const val CODE_SEND_WIFI_INFO_FIXED_KEY = "01"

        /** APP发送 命令号 确认随机密钥*/
        const val CODE_SEND_CHECK_RANDOM_KEY = "02"

        /** APP发送 命令号 配网信息 (随机密钥)*/
        const val CODE_SEND_WIFI_INFO_RANDOM_KEY = "03"

        /** 设备回复 命令类型 心跳包 */
        const val CODE_SEND_HEARTBEAT = "01"

        /************************************APP命令类型***************************************/
        /** 命令 版本协商请求 */
        const val CODE_REPLY_VERSION = "01"

        /** 命令 固定密钥 回复 配网信息解析状态 */
        const val CODE_REPLY_WIFI_INFO_FIXED_KEY = "01"

        /** 命令 随机密钥 回复 解析状态 */
        const val CODE_REPLY_RANDOM_KEY = "02"

        /** 命令 随机密钥 回复 配网信息解析状态 */
        const val CODE_REPLY_WIFI_INFO_RANDOM_KEY = "03"


        /** 帧头 */
        const val FRAME_HEADER = "AAAA"

        const val ERROR = "ERROR"

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TCPInfo

        if (!byteData.contentEquals(other.byteData)) return false
        if (time != other.time) return false
        if (ipAddress != other.ipAddress) return false
        if (port != other.port) return false
        return true
    }

    override fun hashCode(): Int {
        return (byteData.contentHashCode() + time + ipAddress.hashCode() + port).toInt()
    }

    /**
     * 是否RD定义帧头
     * @date: 2020/12/21
     * @param:
     * @return:
     */
    fun isRDFrameHeader(): Boolean {
        return if (byteData.size > 4 && byteData.toHexStr(false).substring(0, 4).equals(FRAME_HEADER, true)) {
            true
        } else {
            ToastUtils.showShort("数据校验错误")
            LogUtils.e("非标准帧头")
            false
        }
    }

    /**
     * 获取帧头
     * @date: 2020/12/21
     * @param:
     * @return:  ByteArray 帧头
     */
    fun getFrameHeader(): String {
        return if (isRDFrameHeader()) {
            FRAME_HEADER
        } else {
            ToastUtils.showShort("数据校验错误")
            ERROR
        }
    }

    /**
     * 获取数据长度
     * @date: 2020/12/21
     * @param:
     * @return: Int 数据长度
     */
    fun getDataLength(): Int {
        //数据包标定长度
        var t = byteData[2].toInt()
        t = t shl 8
        t += byteData[3]

        //校验帧头,数据包标定长度是否与收到数据包长度一致(不包含帧头(2) 不包含自身(2))
        return if (isRDFrameHeader() && t == byteData.size - 4) {
            t
        } else {
            ToastUtils.showShort("数据校验错误")
            -1
        }
    }


    /**
     * 获取命令类型
     * @date: 2020/12/21
     * @param:
     * @return:Byte 命令类型
     */
    fun getOpType(): String {
        return if (getDataLength() != -1) {
            byteArrayOf(0, byteData[4]).toHexStr(false).substring(2, 4)
        } else {
            ERROR
        }
    }

    /**
     * 获取命令号
     * @date: 2020/12/21
     * @param:
     * @return: Byte 命令号
     */
    fun getOpCode(): String {
        return if (getDataLength() != -1) {
            byteArrayOf(0, byteData[5]).toHexStr(false).substring(2, 4)
        } else {
            ERROR
        }
    }

    /**
     * 获取sn
     * @date: 2020/12/21
     * @param:
     * @return: Byte  sn
     */
    fun getSN(): Int {
        return if (getDataLength() != -1) {
            var t = 0
            t = t shl 8
            t += byteData[6]
            t
        } else {
            -1
        }
    }

    /**
     * 获取CRC校验值
     * @return String
     */
    fun getCRCCheckNum(): String {
        val checkNumByteArray = ByteArray(2)

        return if (getDataLength() >= 4) {
            checkNumByteArray[0] = byteData[byteData.size - 2]
            checkNumByteArray[1] = byteData[byteData.size - 1]
            checkNumByteArray.byteArrayToHexStr()
        } else {
            ERROR
        }
    }

    /**
     * 获取设备发送的真实数据内容
     * @date: 2020/12/21
     * @param:
     * @return: ByteArray 数据内容
     */
    fun getDeviceData(): ByteArray {
        return if (getDataLength() < 5) {
            byteArrayOf()
        } else {
            //数组长度 = 数据包长度  - 命令类型(1) - 命令号(1) - sn序号(1) - crc校验位(2)
            val deviceByteArray = ByteArray(getDataLength() - 5)
            deviceByteArray.forEachIndexed { index, _ -> deviceByteArray[index] = byteData[7 + index] }
            deviceByteArray
        }
    }

    fun getStateBoolean(): Boolean {
        return (getDataLength() >= 5 && byteData[byteData.size - 3].toInt() == 1)
    }
}
