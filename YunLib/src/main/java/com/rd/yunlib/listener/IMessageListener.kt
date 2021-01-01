package com.rd.yunlib.listener


/** 默认超时时间 */
private const val DEFAULT_TIME_OUT_VALUE = 10 * 1000L

/**
 * 服务器 MQTT 通讯接口
 * @author D10NG
 * @date on 2020/10/10 10:59 AM
 */
interface IMessageListener {

    /**
     * 成功
     * @param message String
     */
    fun success(message: String)

    /**
     * 失败
     * @param e String
     */
    fun failed(e: String)

    /**
     * 超时无回复
     */
    fun timeout()
}

/**
 * 服务器 MQTT 通讯接口 封装
 * @property createdTime Long
 * @property timeOutValue Long
 * @constructor
 */
class PacketMessageListener constructor(
    // 创建时间
    var createdTime: Long = System.currentTimeMillis(),
    // 超时时间
    var timeOutValue: Long = DEFAULT_TIME_OUT_VALUE
): IMessageListener {
    private lateinit var listenerSuccess: (message: String) -> Unit

    fun onSuccess(listenerSuccess: (message: String) -> Unit) {
        this.listenerSuccess = listenerSuccess
    }
    override fun success(message: String) {
        this.listenerSuccess.invoke(message)
    }

    private lateinit var listenerFailed: (e: String) -> Unit

    fun onFailed(listenerFailed: (e: String) -> Unit) {
        this.listenerFailed = listenerFailed
    }

    override fun failed(e: String) {
        this.listenerFailed.invoke(e)
    }

    private lateinit var listenerTimeOut: () -> Unit

    fun onTimeOut(listenerTimeOut: () -> Unit) {
        this.listenerTimeOut = listenerTimeOut
    }

    override fun timeout() {
        this.listenerTimeOut.invoke()
    }
}