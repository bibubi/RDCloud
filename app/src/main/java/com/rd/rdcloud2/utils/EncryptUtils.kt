package com.rd.rdcloud2.utils

import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 字符串加密工具
 *
 * @author D10NG
 * @date on 2019-06-27 10:10
 */

/** 加密key  */
private const val PASSWORD_ENC_SECRET = "C1ACB03D2D4257BF0A1FA2DA103AF6D6"

private const val KEY_ALGORITHM = "AES"

//private static final String CipherMode = "AES/ECB/PKCS5Padding";使用ECB加密，不需要设置IV，但是不安全
private const val CipherMode = "AES/ECB/NoPadding"//使用CFB加密，需要设置IV

private val CHARSET = Charset.forName("UTF-8")

private const val IV_STR = "1234567812345678"

/**
 * 对字符串加密
 * @param data  源字符串
 * @return  加密后的字符串
 */
fun String.encrypt(): String {
    val keySpec = SecretKeySpec(PASSWORD_ENC_SECRET.toByteArray(CHARSET), KEY_ALGORITHM)
    val cipher = Cipher.getInstance(CipherMode)
//    cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(IV_STR.toByteArray(CHARSET)))
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
//    Log.e("测试", "PASSWORD_ENC_SECRET.toByteArray()=" + PASSWORD_ENC_SECRET.toByteArray(CHARSET).byteArrayToHexStr())
//    Log.e("测试", "IV_STR.toByteArray(CHARSET)=" + IV_STR.toByteArray(CHARSET).byteArrayToHexStr())


    val encrypted = cipher.doFinal(this.toByteArray(CHARSET))
//    Log.e("测试", "encrypted=" + encrypted.byteArrayToHexStr())
    return  encrypted.byteArrayToHexStr()
}

/**
 * 对字符串解密
 * @param data  已被加密的字符串
 * @return  解密得到的字符串
 */
fun String.decrypt(): String {
//    val encrypted1 = Base64.decode(this.toByteArray(CHARSET), Base64.NO_WRAP)
    val keySpec = SecretKeySpec(PASSWORD_ENC_SECRET.toByteArray(CHARSET), KEY_ALGORITHM)
    val cipher = Cipher.getInstance(CipherMode)
//    cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(IV_STR.toByteArray(CHARSET)))
    cipher.init(Cipher.DECRYPT_MODE, keySpec)

    val original = cipher.doFinal(this.toByteArray(CHARSET))
    return String(original, CHARSET)
}

