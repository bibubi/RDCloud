package com.rd.yunlib

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rd.yunlib.bean.LoginResult
import com.rd.yunlib.listener.MessageListenerManager
import com.rd.yunlib.utils.LogUtils
import com.rd.yunlib.utils.getAndroidDeviceUniqueId
import com.rd.yunlib.utils.md5
import com.squareup.moshi.Moshi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONObject
import kotlin.coroutines.resume

/**
 * 云服务管理器
 *
 * @author D10NG
 * @date on 2020/9/24 11:31 AM
 */

/** 瑞德云2.0 MQTT TCP地址 */
public const val SERVER_URL = "tcp://192.168.1.28:4863"

/** 鸿的MQTT */
public const val SERVER_URL_H = "tcp://192.168.1.28:1883"

/** MQTT连接帐号 */
private const val RD_ACCOUNT = "rdmquser"

/** MQTT连接密码 */
private const val RD_PASSWORD = "rdmqpass"

class YunManager constructor(context: Context){

    companion object {

        @Volatile
        private var INSTANCE: YunManager? = null

        @JvmStatic
        fun instance(context: Context): YunManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: YunManager(context).also {
                    INSTANCE = it
                }
            }
    }

    private val TAG = this.javaClass.name

    /** MQTT客户端 */
    private var mqttClient: MqttAndroidClient

    /** 服务器连接状态 */
    var isConnectedLive: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        // 建立唯一ID
        val clientId = context.getAndroidDeviceUniqueId() + System.currentTimeMillis()
        LogUtils.e("UUID:${context.getAndroidDeviceUniqueId()}")
        //val clientId = "android_id_1234"
        // 建立MQTT客户端
        mqttClient = MqttAndroidClient(context, SERVER_URL, clientId)
        // 设置回调信息监听
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                // 连接丢失，在这里执行重连
                LogUtils.e(TAG, "connectionLost")
                isConnectedLive.postValue(false)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                // 接收到的消息
                LogUtils.e(TAG, "messageArrived：topic=$topic, message=${message.toString()}")
                val listener = MessageListenerManager.instance().getListener(topic ?: return)
                listener?.success(message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // publish消息完成
                LogUtils.i(TAG, "deliveryComplete")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                // 连接完成
                LogUtils.i(TAG, "connectComplete：reconnect=$reconnect, serverURI=$serverURI")
            }
        })
        // MQTT连接参数
        val options = MqttConnectOptions().apply {
            // 设置是否清空session，如果是false则表示服务器保留客户端连接记录
            isCleanSession = true
            // 设置连接的用户名
            userName = RD_ACCOUNT
            // 设置连接的密码
            password = RD_PASSWORD.toCharArray()
            // 设置超时时间，单位为秒
            connectionTimeout = 10
            // 设置会话间心跳时间，单位为秒，服务器每隔1.5*20向客户端发消息确认
            keepAliveInterval = 20
            // 是否自动重新连接
            isAutomaticReconnect = true
        }
        // 连接服务器
        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // 连接成功
                LogUtils.i(TAG, "连接服务器成功")
//                mqttClient.subscribe("testtop",0)
                isConnectedLive.postValue(true)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // 连接失败
                LogUtils.i(TAG, "连接服务器失败，${exception.toString()}")
            }
        })
    }

    /**
     * 登录
     * @param username 用户注册的帐号
     * @param password MD5后的密码
     * @param reconnection 是否重连，iOS只要没退出后台重新打开就要重连，reconnection大于等于1重连，重连session不会变
     * @return Boolean
     */
    suspend fun login(username: String, password: String, reconnection: Int): LoginResult? {
        LogUtils.e("测试", "name=$username, pass=$password, md5=${password.md5()}")
        return suspendCancellableCoroutine { cont ->
            val jsonObj = JSONObject().apply {
                put("username", username)
                put("password", password.md5())
                put("reconnection", reconnection)
            }
            val topic = "login/user"
            mqttClient.publish(topic, MqttMessage(jsonObj.toString().toByteArray()))
            MessageListenerManager.instance().addListener(topic) {
                onSuccess { message ->
                    val adapter = Moshi.Builder().build().adapter(LoginResult::class.java)
                    if (cont.isActive) cont.resume(adapter.fromJson(message))
                }
                onFailed { e ->
                    LogUtils.i(TAG, e)
                    if (cont.isActive) cont.resume(null)
                }
                onTimeOut {
                    if (cont.isActive) cont.resume(null)
                }
            }
        }
    }

    suspend fun test(): String {
        return suspendCancellableCoroutine { cont ->
            val topic = "testtop"
            mqttClient.publish(topic, MqttMessage("testtop2333".toByteArray()))
            MessageListenerManager.instance().addListener(topic) {
                onSuccess { message ->
                    LogUtils.e(message)
                }
                onFailed { e ->
                    LogUtils.i(TAG, e)
                }
                onTimeOut {
                    LogUtils.e("TimeOut")
                }
            }
        }
    }

    /**
     * 绑定（设备要先发送请求，后APP才能绑定，有效时间2分钟内）
     * @param ssid String 路由器SSID
     * @param password String 路由器密码
     * @param deviceIp String 设备局域网的ip
     * @param phoneIp String 手机局域网的ip
     * @return Boolean
     */
    suspend fun bindDevice(
        ssid: String,
        password: String,
        deviceIp: String,
        phoneIp: String
    ): Boolean {
        return suspendCancellableCoroutine { cont ->
            val jsonObj = JSONObject().apply {
                put("ssid", ssid)
                put("password", password)
                put("deviceip", deviceIp)
                put("phoneip", phoneIp)
            }
            val topic = "devices/bind"
            mqttClient.publish(topic, MqttMessage(jsonObj.toString().toByteArray()))
            MessageListenerManager.instance().addListener(topic) {
                onSuccess { message ->
                    // todo
                }
                onFailed { e ->
                    LogUtils.i(TAG, e)
                }
                onTimeOut {
                }
            }
        }
    }


}