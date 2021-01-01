package com.rd.rdcloud2.utils

import com.blankj.utilcode.util.ConvertUtils
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


/**
 * AES加解密
 * @author mR2hao
 * @date 2020/12/15
 */
object AESCryptUtil {

    private val ECB_CIPHER = Cipher.getInstance("AES/ECB/NoPadding")
    private val CBC_CIPHER = Cipher.getInstance("AES/CBC/NoPadding")


    /**
     * AES ECB加密 [*待加密内容 需是16进制字符串]
     * @see  hexStringToByteArray
     * @param input String  待加密内容
     * @param key String 密钥
     * @return String 加密后字符串
     */
    fun encryptECB(input: String, key: String): String {
        return this.encryptECB(input.hexStringToByteArray(), key.toByteArray())
    }

    /**
     * AES ECB加密
     * @param input ByteArray  待加密内容
     * @param key ByteArray 密钥
     * @return String 加密后字符串
     */
    fun encryptECB(input: ByteArray, key: ByteArray): String {
        //创建cipher对象
        //初始化:加密/解密
        val keySpec = SecretKeySpec(key, "AES")
        ECB_CIPHER.init(Cipher.ENCRYPT_MODE, keySpec)
        //加密
        val encrypt = ECB_CIPHER.doFinal(input)
        return ConvertUtils.bytes2HexString(encrypt)
    }

    /**
     * AES ECB解密 [*待解密内容 需是16进制字符串]
     * @param input String 需要解密内容
     * @param key String 密钥
     * @return String 解密后字符串
     */
    fun decryptECB(input: String, key: String): String {
        return decryptECB(input.hexStringToByteArray(), key.toByteArray())
    }

    /**
     * AES ECB解密
     * @param input ByteArray 需要加密内容
     * @param key ByteArray 密钥
     * @return String 解密后字符串
     */
    fun decryptECB(input: ByteArray, key: ByteArray): String {
        //创建cipher对象
        //初始化:加密/解密
        val keySpec = SecretKeySpec(key, "AES")
        ECB_CIPHER.init(Cipher.DECRYPT_MODE, keySpec)
        val encrypt = ECB_CIPHER.doFinal(input)
        return ConvertUtils.bytes2HexString(encrypt)
    }

    /**
     * AES CBC加密  [*待加密内容、偏移量需是16进制字符串]
     * @see  hexStringToByteArray
     * @param input String 需要加密内容
     * @param key String 密钥
     * @param ivParameterSpec String 偏移量
     * @return String 加密后字符串
     */
    fun encryptCBC(input: String, key: String, ivParameterSpec: String): String {
        return encryptCBC(
                input.hexStringToByteArray(),
                key.toByteArray(),
                ivParameterSpec.hexStringToByteArray()
        )
    }

    /**
     * AES CBC加密
     * @see  hexStringToByteArray
     * @param input ByteArray  待加密内容
     * @param key ByteArray 密钥
     * @param ivParameterSpec ByteArray 偏移量
     * @return String 加密后字符串
     */
    fun encryptCBC(input: ByteArray, key: ByteArray, ivParameterSpec: ByteArray): String {
        //创建cipher对象
        //初始化:加密/解密
        val keySpec = SecretKeySpec(key, "AES")
        val iv = IvParameterSpec(ivParameterSpec)
        CBC_CIPHER.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        //加密
        val encrypt = CBC_CIPHER.doFinal(input)
        return ConvertUtils.bytes2HexString(encrypt)
    }

    /**
     * AES CBC解密  [*待加密内容、偏移量需是16进制字符串]
     * @see  hexStringToByteArray
     * @param input String 待解密内容
     * @param key String 密钥
     * @param ivParameterSpec String 偏移量
     * @return String 加密后字符串
     */
    fun decryptCBC(input: String, key: String, ivParameterSpec: String): String {
        return decryptCBC(
                input.hexStringToByteArray(),
                key.toByteArray(),
                ivParameterSpec.hexStringToByteArray()
        )
    }

    /**
     * AES CBC加密
     * @see  hexStringToByteArray
     * @param input ByteArray  待解密内容
     * @param key ByteArray 密钥
     * @param ivParameterSpec ByteArray 偏移量(16进制字符串)
     * @return String 加密后字符串
     */
    fun decryptCBC(input: ByteArray, key: ByteArray, ivParameterSpec: ByteArray): String {
        //创建cipher对象
        //初始化:加密/解密
        val keySpec = SecretKeySpec(key, "AES")
        val iv = IvParameterSpec(ivParameterSpec)
        CBC_CIPHER.init(Cipher.DECRYPT_MODE, keySpec, iv)
        //加密
        val encrypt = CBC_CIPHER.doFinal(input)
        return ConvertUtils.bytes2HexString(encrypt)
    }

}