package com.rd.yunlib.utils

import android.util.Log


/**
 * 打印工具
 */
object LogUtils {

    // 日志控制开关, 当正式发布时,请置为false
    private var DEBUG: Boolean = true
    // 默认Tag
    private const val TAG = "rdcloudlib"

    /** Application onCreate 中初始化  */
    fun initLog(b: Boolean) {
        DEBUG = b
    }

    fun isDebug() = DEBUG

    fun i(tag: String, msg: String) {
        if (DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun i(msg: String) {
        if (DEBUG) {
            Log.i(TAG, msg)
        }
    }


    fun d(tag: String, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun d(msg: String) {
        if (DEBUG) {
            Log.d(TAG, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        if (DEBUG) {
            Log.e(TAG, msg)
        }
    }

    fun et(tag: String, msg: String, throwable: Throwable) {
        if (DEBUG) {
            Log.e(tag, msg, throwable)
        }
    }


    fun v(tag: String, msg: String) {
        if (DEBUG) {
            Log.v(tag, msg)
        }
    }

    fun v(msg: String) {
        if (DEBUG) {
            Log.v(TAG, msg)
        }
    }
}
