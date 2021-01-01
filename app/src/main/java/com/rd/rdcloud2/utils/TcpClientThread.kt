package com.rd.rdcloud2.utils

import android.os.Handler
import android.os.Message
import com.blankj.utilcode.util.LogUtils
import com.rd.rdcloud2.bean.TCPInfo
import java.net.Socket

/**
 * TCP 客户端
 *
 * @author D10NG
 * @date on 2019-12-09 14:37
 */
class TcpClientThread constructor(
        private val mHandler: Handler,
        private val ipAddress: String,
        private val port: Int
) : Thread() {

    private var socket: Socket? = null

    companion object {
        const val CONNECT_SUCCESS = 31
        const val CONNECT_FAILED = 32
        const val DISCONNECT = 33
        const val RECEIVE_MSG = 34
    }

    override fun run() {
        super.run()

        try {
            socket = Socket(ipAddress, port)
            socket?.reuseAddress = true
        } catch (e: Exception) {
            LogUtils.e(e.message)
            mHandler.sendEmptyMessage(CONNECT_FAILED)
            return
        }
        mHandler.sendEmptyMessage(CONNECT_SUCCESS)

        val inputStream = socket?.getInputStream()

        while (socket?.isConnected == true) {
            val buffer = ByteArray(1024)
            val len =
                    try {
                        inputStream?.read(buffer) ?: 0
                    } catch (e: Exception) {
                        -1
                    }
            if (len == -1) {
                break
            }
            if (len > 0) {
                // 包装
                val receive = TCPInfo()
                receive.byteData = buffer.copyOfRange(0, len)
                receive.time = System.currentTimeMillis()
                receive.ipAddress = ipAddress
                receive.port = port

                val m = Message.obtain()
                m.what = RECEIVE_MSG
                m.obj = receive
                mHandler.sendMessage(m)
            }
        }
        mHandler.sendEmptyMessage(DISCONNECT)
    }

    fun send(address: String, toPort: Int, data: ByteArray) {
        send(data)
    }

    fun send(data: ByteArray) {
        Thread(Runnable {
            if (socket?.isConnected == true) {
                socket?.getOutputStream()?.write(data)
                socket?.getOutputStream()?.flush()
            }
        }).start()
    }

    fun isConnected(): Boolean = socket?.isConnected == true

    fun close() {
        socket?.close()
    }

}
