package com.rd.yunlib.listener

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 通讯请求回调管理器
 *
 * @author D10NG
 * @date on 2020/10/10 11:06 AM
 */
class MessageListenerManager {

    companion object {

        @Volatile
        private var INSTANCE: MessageListenerManager? = null

        @JvmStatic
        fun instance() : MessageListenerManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MessageListenerManager().also {
                    INSTANCE = it
                }
            }
    }

    /** 消息缓存存储器 */
    private val listenerMap = mutableMapOf<String, PacketMessageListener>()

    init {
        // 启动子线程去不断检查是否有超时的
        GlobalScope.launch {
            while (isActive) {
                val timeOutList = mutableListOf<String>()
                for (key in listenerMap.keys) {
                    val curTime = System.currentTimeMillis()
                    val listener = listenerMap[key]?: continue
                    if (curTime - (listener.createdTime + listener.timeOutValue) >= 0) {
                        // 已经超时
                        listener.timeout()
                        timeOutList.add(key)
                    }
                }
                // 移除已触发的超时请求监听
                for (key in timeOutList) {
                    listenerMap.remove(key)
                }
                delay(900)
            }
        }
    }

    /**
     * 添加回调
     * @param topic String 主题
     * @param listener PacketMessageListener 监听器
     */
    fun addListener(topic: String, listener: PacketMessageListener.() -> Unit) {
        listenerMap[topic]?.failed("重复请求！")
        val packListener = PacketMessageListener()
        packListener.listener()
        listenerMap[topic] = packListener
    }

    /**
     * 获取回调
     * @param topic String 主题
     * @return PacketMessageListener? 监听器
     */
    fun getListener(topic: String): PacketMessageListener? {
        return listenerMap[topic]
    }

    /**
     * 移除监听器
     * @param topic String 主题
     */
    fun removeListener(topic: String) {
        listenerMap.remove(topic)
    }
}