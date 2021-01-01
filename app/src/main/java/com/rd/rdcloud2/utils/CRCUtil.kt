package com.rd.rdcloud2.utils

import java.util.*

/**
 * @author mR2hao
 * @date 2020/12/23
 */
object CRCUtil {
    private val TABLE = charArrayOf(0x0000.toChar(), 0xC0C1.toChar(), 0xC181.toChar(), 0x0140.toChar(), 0xC301.toChar(), 0x03C0.toChar(), 0x0280.toChar(), 0xC241.toChar(), 0xC601.toChar(), 0x06C0.toChar(), 0x0780.toChar(), 0xC741.toChar(),
            0x0500.toChar(), 0xC5C1.toChar(), 0xC481.toChar(), 0x0440.toChar(), 0xCC01.toChar(), 0x0CC0.toChar(), 0x0D80.toChar(), 0xCD41.toChar(), 0x0F00.toChar(), 0xCFC1.toChar(), 0xCE81.toChar(), 0x0E40.toChar(), 0x0A00.toChar(), 0xCAC1.toChar(), 0xCB81.toChar(), 0x0B40.toChar(),
            0xC901.toChar(), 0x09C0.toChar(), 0x0880.toChar(), 0xC841.toChar(), 0xD801.toChar(), 0x18C0.toChar(), 0x1980.toChar(), 0xD941.toChar(), 0x1B00.toChar(), 0xDBC1.toChar(), 0xDA81.toChar(), 0x1A40.toChar(), 0x1E00.toChar(), 0xDEC1.toChar(), 0xDF81.toChar(), 0x1F40.toChar(),
            0xDD01.toChar(), 0x1DC0.toChar(), 0x1C80.toChar(), 0xDC41.toChar(), 0x1400.toChar(), 0xD4C1.toChar(), 0xD581.toChar(), 0x1540.toChar(), 0xD701.toChar(), 0x17C0.toChar(), 0x1680.toChar(), 0xD641.toChar(), 0xD201.toChar(), 0x12C0.toChar(), 0x1380.toChar(), 0xD341.toChar(),
            0x1100.toChar(), 0xD1C1.toChar(), 0xD081.toChar(), 0x1040.toChar(), 0xF001.toChar(), 0x30C0.toChar(), 0x3180.toChar(), 0xF141.toChar(), 0x3300.toChar(), 0xF3C1.toChar(), 0xF281.toChar(), 0x3240.toChar(), 0x3600.toChar(), 0xF6C1.toChar(), 0xF781.toChar(), 0x3740.toChar(),
            0xF501.toChar(), 0x35C0.toChar(), 0x3480.toChar(), 0xF441.toChar(), 0x3C00.toChar(), 0xFCC1.toChar(), 0xFD81.toChar(), 0x3D40.toChar(), 0xFF01.toChar(), 0x3FC0.toChar(), 0x3E80.toChar(), 0xFE41.toChar(), 0xFA01.toChar(), 0x3AC0.toChar(), 0x3B80.toChar(), 0xFB41.toChar(),
            0x3900.toChar(), 0xF9C1.toChar(), 0xF881.toChar(), 0x3840.toChar(), 0x2800.toChar(), 0xE8C1.toChar(), 0xE981.toChar(), 0x2940.toChar(), 0xEB01.toChar(), 0x2BC0.toChar(), 0x2A80.toChar(), 0xEA41.toChar(), 0xEE01.toChar(), 0x2EC0.toChar(), 0x2F80.toChar(), 0xEF41.toChar(),
            0x2D00.toChar(), 0xEDC1.toChar(), 0xEC81.toChar(), 0x2C40.toChar(), 0xE401.toChar(), 0x24C0.toChar(), 0x2580.toChar(), 0xE541.toChar(), 0x2700.toChar(), 0xE7C1.toChar(), 0xE681.toChar(), 0x2640.toChar(), 0x2200.toChar(), 0xE2C1.toChar(), 0xE381.toChar(), 0x2340.toChar(),
            0xE101.toChar(), 0x21C0.toChar(), 0x2080.toChar(), 0xE041.toChar(), 0xA001.toChar(), 0x60C0.toChar(), 0x6180.toChar(), 0xA141.toChar(), 0x6300.toChar(), 0xA3C1.toChar(), 0xA281.toChar(), 0x6240.toChar(), 0x6600.toChar(), 0xA6C1.toChar(), 0xA781.toChar(), 0x6740.toChar(),
            0xA501.toChar(), 0x65C0.toChar(), 0x6480.toChar(), 0xA441.toChar(), 0x6C00.toChar(), 0xACC1.toChar(), 0xAD81.toChar(), 0x6D40.toChar(), 0xAF01.toChar(), 0x6FC0.toChar(), 0x6E80.toChar(), 0xAE41.toChar(), 0xAA01.toChar(), 0x6AC0.toChar(), 0x6B80.toChar(), 0xAB41.toChar(),
            0x6900.toChar(), 0xA9C1.toChar(), 0xA881.toChar(), 0x6840.toChar(), 0x7800.toChar(), 0xB8C1.toChar(), 0xB981.toChar(), 0x7940.toChar(), 0xBB01.toChar(), 0x7BC0.toChar(), 0x7A80.toChar(), 0xBA41.toChar(), 0xBE01.toChar(), 0x7EC0.toChar(), 0x7F80.toChar(), 0xBF41.toChar(),
            0x7D00.toChar(), 0xBDC1.toChar(), 0xBC81.toChar(), 0x7C40.toChar(), 0xB401.toChar(), 0x74C0.toChar(), 0x7580.toChar(), 0xB541.toChar(), 0x7700.toChar(), 0xB7C1.toChar(), 0xB681.toChar(), 0x7640.toChar(), 0x7200.toChar(), 0xB2C1.toChar(), 0xB381.toChar(), 0x7340.toChar(),
            0xB101.toChar(), 0x71C0.toChar(), 0x7080.toChar(), 0xB041.toChar(), 0x5000.toChar(), 0x90C1.toChar(), 0x9181.toChar(), 0x5140.toChar(), 0x9301.toChar(), 0x53C0.toChar(), 0x5280.toChar(), 0x9241.toChar(), 0x9601.toChar(), 0x56C0.toChar(), 0x5780.toChar(), 0x9741.toChar(),
            0x5500.toChar(), 0x95C1.toChar(), 0x9481.toChar(), 0x5440.toChar(), 0x9C01.toChar(), 0x5CC0.toChar(), 0x5D80.toChar(), 0x9D41.toChar(), 0x5F00.toChar(), 0x9FC1.toChar(), 0x9E81.toChar(), 0x5E40.toChar(), 0x5A00.toChar(), 0x9AC1.toChar(), 0x9B81.toChar(), 0x5B40.toChar(),
            0x9901.toChar(), 0x59C0.toChar(), 0x5880.toChar(), 0x9841.toChar(), 0x8801.toChar(), 0x48C0.toChar(), 0x4980.toChar(), 0x8941.toChar(), 0x4B00.toChar(), 0x8BC1.toChar(), 0x8A81.toChar(), 0x4A40.toChar(), 0x4E00.toChar(), 0x8EC1.toChar(), 0x8F81.toChar(), 0x4F40.toChar(),
            0x8D01.toChar(), 0x4DC0.toChar(), 0x4C80.toChar(), 0x8C41.toChar(), 0x4400.toChar(), 0x84C1.toChar(), 0x8581.toChar(), 0x4540.toChar(), 0x8701.toChar(), 0x47C0.toChar(), 0x4680.toChar(), 0x8641.toChar(), 0x8201.toChar(), 0x42C0.toChar(), 0x4380.toChar(), 0x8341.toChar(),
            0x4100.toChar(), 0x81C1.toChar(), 0x8081.toChar(), 0x4040.toChar())

    /**
     * 获取校验值
     * @date 2020/12/23
     * @param data ByteArray 待校验数据
     * @return
     */
    fun getCRC16(data: ByteArray): Int {
        var chCRC = 0xffff
        for (element in data) {
            chCRC = chCRC shr 8 xor TABLE[chCRC xor element.toInt() and 0xff].toInt()
        }
        return chCRC
    }


    /**
     * 从十六进制字符串中 计算CRC校验值
     * @date 2020/12/23
     * @param
     * @return
     */
    fun String.getCRC16(): String {
        val tmpByteArray = this.replace(" ", "").hexStringToByteArray()
        return Integer.toHexString(getCRC16(tmpByteArray)).upToNStr(4).toUpperCase(Locale.getDefault())
    }


}