package com.rd.yunlib.listener

import android.os.Handler
import android.os.Message

import androidx.appcompat.app.AppCompatActivity

import java.lang.ref.WeakReference

/**
 * 封装Handler子类
 * $ 解决handler内存泄漏问题
 *
 * @author D10NG
 * @date on 2019-09-28 11:11
 */
class BaseHandler(c: AppCompatActivity, b: BaseHandlerCallBack) : Handler() {

    private val act: WeakReference<AppCompatActivity> = WeakReference(c)
    private val callBack: WeakReference<BaseHandlerCallBack> = WeakReference(b)

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val c = act.get()
        if (c != null) {
            callBack.get()?.callBack(msg)
        }
    }

    interface BaseHandlerCallBack {
        fun callBack(msg: Message)
    }
}

