package com.rd.rdcloud2.constant

/**
 *
 * @author mR2hao
 * @date 2020/12/23
 */
object VersionConstant {
    /**
     * 加密方式
     */
    enum class EncryptType(val index: Int, val des: String) {
        /**
         * 固定密钥
         */
        AES_FIXED(1, "固定密钥"),

        /**
         * 随机密钥
         */
        AES_RANDOM(2, "随机密钥"),

        /**
         * DH交换密钥AES加密
         */
        AES_DH(3, "DH交换密钥"),

        /**
         * 错误
         */
        ERROR(999, "错误");


        companion object {
            /**
             * 获取描述
             * @param index Int 序号
             * @return String 描述内容
             */
            fun getDes(index: Int): String {
                return when (index) {
                    1 -> AES_FIXED.des
                    2 -> AES_RANDOM.des
                    3 -> AES_DH.des
                    else -> ERROR.des
                }
            }

            /**
             * 通过序号获取EncryptType
             * @param index Int
             * @return EncryptType?
             */
            fun parseIndex(index: Int): EncryptType {
                return when (index) {
                    1 -> AES_FIXED
                    2 -> AES_RANDOM
                    3 -> AES_DH
                    else -> ERROR
                }
            }

        }
    }

    /**
     * AES加密方式
     */
    enum class AESType(val index: Int, val des: String) {
        /**
         * ECB加密
         */
        AES_ECB(1, "AES-ECB加密"),

        /**
         * CBC加密
         */
        AES_CBC(2, "AES-CBC加密"),

        /**
         * 错误
         */
        ERROR(999, "错误");

        companion object {
            /**
             * 获取描述
             * @param index Int 序号
             * @return String 描述内容
             */
            fun getDes(index: Int): String {
                return when (index) {
                    1 -> AES_ECB.des
                    2 -> AES_CBC.des
                    else -> ERROR.des
                }
            }

            /**
             * 通过序号获取 AESType
             * @param index Int
             * @return AESType?
             */
            fun parseIndex(index: Int): AESType {
                return when (index) {
                    1 -> AES_ECB
                    2 -> AES_CBC
                    else -> ERROR
                }
            }

        }
    }

    /**
     * 加密位数
     */
    enum class AESBitType(val index: Int, val des: String) {
        /**
         * 128位加密
         */
        AES_128(1, "128位"),

        /**
         * 256位加密
         */
        AES_256(2, "256位"),

        /**
         * 错误
         */
        ERROR(999, "错误");


        companion object {
            /**
             * 获取描述
             * @param index Int 序号
             * @return String 描述内容
             */
            fun getDes(index: Int): String {
                return when (index) {
                    1 -> AES_128.des
                    2 -> AES_256.des
                    else -> ERROR.des

                }
            }

            /**
             * 通过序号获取 AESBitType
             * @param index Int
             * @return AESBitType
             */
            fun parseIndex(index: Int): AESBitType {
                return when (index) {
                    1 -> AES_128
                    2 -> AES_256
                    else -> ERROR
                }
            }
        }
    }
}