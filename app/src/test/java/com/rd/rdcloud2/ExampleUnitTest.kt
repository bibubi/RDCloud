package com.rd.rdcloud2

import com.rd.rdcloud2.utils.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.charset.Charset

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    /** 固定密钥 128位 */
    private val aes128Key: String = "1234561234567890"

    /** 固定密钥 256位*/
    private val aes256Key: String = "12345612345678901234561234567890"

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun en() {
        val enString = "C1 AC B0 3D 2D 42 57 BF 0A 1F A2 DA 10 3A F6 D6 57 24 AF 30 8C 38 16 60 FE 9B 50 80 30 0E B0 F3 E9 83 BC E0 15 1E 1A C1 29 96 BE 38 49 F7 5B A9 EA DE 90 6A AD 88 F0 69 72 B2 F9 C2 27 A5 16 3F"

        val eStr2 = AESCryptUtil.decryptECB(enString.replace(" ", ""), aes128Key)


        println(eStr2)
    }

    @Test
    fun chineseTest(): Unit {
        val strings = arrayListOf<String>("wsdfsdfgsadfsadf", "12332132131", "asd132123", "w我sfsdf", "12我313", "s1s12d3f1w我ef")
        strings.forEach {
            println(it.isContainChinese())
        }

       println("Default Charset=" + Charset.defaultCharset());

        var wifiName = "RD-100YUN-2.4G"
        wifiName = "我是ef" //ce d2 ca c7 d6 d0 ce c4

        wifiName = if (!wifiName.isContainChinese()) {
            wifiName.asciiToHexString().upToNStrInBack(28 * 2, "f")
        } else {
            wifiName.toByteArray(Charset.forName("GBK")).byteArrayToHexStr().upToNStrInBack(28 * 2, "f")
        }

        println("wifiName:${wifiName.addSpace()}")
    }
}