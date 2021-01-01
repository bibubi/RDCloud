package com.rd.rdcloud2.activity

import NetUtils
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.rd.rdcloud2.R
import com.rd.rdcloud2.bean.TCPInfo
import com.rd.rdcloud2.bean.WifiInfo
import com.rd.rdcloud2.constant.VersionConstant
import com.rd.rdcloud2.databinding.ActivityTcpTestBinding
import com.rd.rdcloud2.model.WiFiModel
import com.rd.rdcloud2.utils.*
import com.rd.rdcloud2.utils.CRCUtil.getCRC16
import kotlinx.coroutines.*
import java.nio.charset.Charset

/**
 *
 * @author mR2hao
 * @date 2020/12/18
 */
class TCPTestActivity : BaseActivity() {
    private lateinit var mBinding: ActivityTcpTestBinding
    private lateinit var mWiFiModel: WiFiModel

    /** wifi信息 */
    private val mWiFiInfo = WifiInfo()

    /** TCP客户端 */
    private var mTcpClient: TcpClientThread? = null

    /** 储存设备版本号 */
    private lateinit var versionByteArray: ByteArray

    /** 序号 */
    private var sn: Int = 0

    /** 加密方式 */
    private lateinit var encryptType: VersionConstant.EncryptType

    /** 加密模式 */
    private lateinit var aesType: VersionConstant.AESType

    /** 加密位数 */
    private lateinit var aesBitType: VersionConstant.AESBitType


    companion object {
        private const val IS_LIANG = false
        private const val SERVER_IP = "192.168.4.1"
        private const val SERVER_PORT = 18000

        private const val SERVER_IP_L = "192.168.1.138"
        private const val SERVER_PORT_L = 1000

        /** 心跳间隔时间 */
        private const val HEARTBEAT_TIME = 1000L * 3

        /** SSID / PWD固定长度 (字节) */
        private const val FIXED_LEN = 28

        /** 固定密钥 128位 */
        private const val aes128Key: String = "1234561234567890"

        /** 固定密钥 256位*/
        private const val aes256Key: String = "12345612345678901234561234567890"

        /** 加密用随机密钥 */
        private const val randomKey = "52442d31303059554e2d322e3447ffff"

        /** CBC加密偏移量 */
        private const val ivSpec = "22222222222222222222222222222222"

        private const val P_ACCESS_FINE_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tcp_test)

        mWiFiModel = ViewModelProvider(this).get(WiFiModel::class.java)


        mBinding.apply {
            wifiInfo = mWiFiModel
            lifecycleOwner = this@TCPTestActivity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTcpClient?.close()
    }

    override fun onStart() {
        super.onStart()
        // 检查定位权限
        if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            LogUtils.e("没有权限")

            reqPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), P_ACCESS_FINE_LOCATION)
        } else {
            mBinding.btnStartTcp.isEnabled = checkWifiInfoIsOk()
        }
    }

    /** 检查Wi-Fi信息是否正确 */
    private fun checkWifiInfoIsOk(): Boolean {
        val manager = NetUtils.getConnectionInfo(this)
        if (manager == null || NetUtils.getSsid(manager)?.contains("unknown ssid", true) == true) {
            // 没有连接到WI-FI

            LogUtils.e("没有连接到WI-FI ${NetUtils.getSsid(manager)}")
            mWiFiModel.wifiName.value = NetUtils.getSsid(manager)
            ToastUtils.showShort("请先连接WI-FI  ${NetUtils.getSsid(manager)}")
            return false
        }
        if (NetUtils.is5GWifiConnected(this)) {
            // 连接了5G WI-FI
            LogUtils.e("请不要连接5G WI-FI")
            ToastUtils.showShort("请不要连接5G WI-FI")
            mWiFiModel.wifiName.value = NetUtils.getSsid(manager)
            return false
        }

        mWiFiInfo.ssid = NetUtils.getSsid(manager) ?: ""
        mWiFiInfo.ip = NetUtils.getWifiIP(manager)
        //TODO 测试固定使用RD-100YUN-2.4G
//        mBinding.tvWifiName.text = "RD-100YUN-2.4G"
//        mBinding.tvWifiIp.text = mWiFiInfo.ip
        mWiFiModel.wifiName.value = mWiFiInfo.ssid
        mWiFiModel.wifiIp.value = mWiFiInfo.ip
        LogUtils.e("当前IP:${mWiFiInfo.ip}")
        return true
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val pass = !grantResults.contains(PackageManager.PERMISSION_DENIED)
        when (requestCode) {
            P_ACCESS_FINE_LOCATION -> {
                if (pass) {
                    mBinding.btnStartTcp.isEnabled = checkWifiInfoIsOk()
                }
            }
        }
    }

    /**
     * 建立tcp连接
     * @param view View
     */
    fun createTCP(view: View) {
        // 已连上设备热点
        initThread()
    }


    /** 初始化TCP */
    private fun initThread() {
        synchronized(this) {
            mTcpClient?.close()
            mTcpClient = if (IS_LIANG) {
                TcpClientThread(mHandler, SERVER_IP_L, SERVER_PORT_L)

            } else {
                TcpClientThread(mHandler, SERVER_IP, SERVER_PORT)
            }
            mTcpClient?.start()
        }
    }

    override fun callBack(msg: Message) {
        super.callBack(msg)

        when (msg.what) {
            TcpClientThread.CONNECT_FAILED -> {
                LogUtils.e("TCP连接失败")
                addDebugText("TCP连接失败...")
            }

            TcpClientThread.CONNECT_SUCCESS -> {
                LogUtils.e("TCP连接成功")
                addDebugText("TCP连接成功...\n发送版本协商请求...")
                sendMsg(TCPInfo.APP_TYPE_VERSION, TCPInfo.CODE_SEND_CHECK_VERSION)
            }

            TcpClientThread.RECEIVE_MSG -> {
                val receive = msg.obj as TCPInfo
                val dataStr = receive.byteData.toHexStr(true)

                addDebugText("--> $dataStr")

                LogUtils.e("--> $dataStr\n"
                        + "长度:${receive.getDataLength()}\n"
                        + "命令类型:${receive.getOpType()}\n"
                        + "命令号:${receive.getOpCode()}\n"
                        + "SN:${receive.getSN().toString().upToNStr(2)}\n"
                        + "原始数据:${receive.getDeviceData().byteArrayToHexStr().addSpace()}\n"
                        + "校验:${receive.getCRCCheckNum()}")


                when (receive.getOpType()) {
                    //协商版本 0x81
                    TCPInfo.DEVICE_TYPE__VERSION -> {
                        if (receive.getOpCode() == TCPInfo.CODE_REPLY_VERSION) {
                            //回复版本
                            replyDeviceVersion(receive)

                        }
                    }

                    //配网 0x82
                    TCPInfo.DEVICE_TYPE_WIFI_INFO -> {
                        when (receive.getOpCode()) {

                            //固定密钥发送配网信息后返回
                            TCPInfo.CODE_REPLY_WIFI_INFO_FIXED_KEY -> {
                                if (receive.getStateBoolean()) {
                                    addDebugText("设备解析数据成功")
                                } else {
                                    addDebugText("设备解析数据失败")
                                }
                            }

                            //设备随机密钥解析成功
                            TCPInfo.CODE_REPLY_RANDOM_KEY -> {
                                if (receive.getStateBoolean()) {
                                    addDebugText("设备解析数据成功")
                                } else {
                                    addDebugText("设备解析数据失败")
                                }
                                addDebugText("设备解析随机密钥成功")
                                //采用随机密钥 发送WiFi信息
                                sendWiFiInfo(true)
                            }

                            TCPInfo.CODE_REPLY_WIFI_INFO_RANDOM_KEY -> {

                            }


                        }
                    }
                }
            }

            TcpClientThread.DISCONNECT -> {
                LogUtils.e("TCP连接断开")
                addDebugText("TCP连接断开...")
            }
        }
    }

    /**
     * 校验设备版本号 并回复设备
     * @date 2020/12/21
     * @param tcpInfo  ReceiveInfo
     */
    private fun replyDeviceVersion(tcpInfo: TCPInfo) {

        if (tcpInfo.getDeviceData().size >= 4) {
            val sb = StringBuilder("v")
            versionByteArray = tcpInfo.getDeviceData()

            LogUtils.e(versionByteArray.byteArrayToHexStr())
            //组成版本号
            for (i in tcpInfo.getDeviceData().indices) {
                val temp = tcpInfo.getDeviceData()[i].toInt()
                if (temp != 0) {
                    sb.append(temp)
                    if (i != tcpInfo.getDeviceData().size - 1) {
                        sb.append(".")
                    }
                }
            }

            //解析协议版本
            encryptType = VersionConstant.EncryptType.parseIndex(versionByteArray[1].toInt())
            aesType = VersionConstant.AESType.parseIndex(versionByteArray[2].toInt())
            aesBitType = VersionConstant.AESBitType.parseIndex(versionByteArray[3].toInt())

            val debugString = "当前设备版本: $sb -- ${encryptType.des} ${aesType.des} ${aesBitType.des}"
            addDebugText(debugString)
            LogUtils.e(debugString)
            addDebugText("应答协商结果...")


            //采用后 进行密钥协商
            when (encryptType) {
                //固定密钥
                VersionConstant.EncryptType.AES_FIXED,
                    //随机密钥
                VersionConstant.EncryptType.AES_RANDOM -> {
                    //TODO 回复协商结果,这里写死 01 - 采用
                    sendMsg(TCPInfo.APP_TYPE_VERSION, TCPInfo.CODE_SEND_ACCEPT_VERSION, "01${versionByteArray.byteArrayToHexStr()}")

                    if (encryptType == VersionConstant.EncryptType.AES_FIXED) {
                        //采用固定密钥 直接发送WiFi信息
                        sendWiFiInfo()
                    } else {
                        //TODO randomKey 由APP随机生成 然后再通过固定密钥加密 最后发给设备
                        //将随机密钥发送出去
                        sendMsg(TCPInfo.APP_TYPE_WIFI_INFO, TCPInfo.CODE_SEND_CHECK_RANDOM_KEY, encryptDataStr(randomKey))
                    }

                    //开始发送心跳包
                    startHeartbeat()
                }


                //DH交换密钥
                VersionConstant.EncryptType.AES_DH -> {
                    addDebugText("暂不支持DH交换,拒绝采用")
                    sendMsg(TCPInfo.APP_TYPE_VERSION, TCPInfo.CODE_SEND_ACCEPT_VERSION, "0000000000")
                }

                else -> {
                    return
                }
            }

        } else {
            addDebugText("设备回复格式错误...")
            //如果不采用,发00 协商版本号也全部为0 -> 00000000
            sendMsg(TCPInfo.APP_TYPE_VERSION, TCPInfo.CODE_SEND_ACCEPT_VERSION, "0000000000")
        }
    }


    /**
     * 发送配网信息
     * @date 2020/12/22
     */
    private fun sendWiFiInfo(isUseRandomKey: Boolean = false) {
        addDebugText("发送配网信息...")


        var wifiName = mWiFiModel.wifiName.value.toString()
        wifiName = "RD-100YUN-2.4G"
        wifiName = if (!wifiName.isContainChinese()) {
            wifiName.asciiToHexString().upToNStrInBack(FIXED_LEN * 2, "f")
        } else {
            wifiName.toByteArray(Charset.forName("GBK")).byteArrayToHexStr().upToNStrInBack(FIXED_LEN * 2, "f")
        }

//        LogUtils.e("wifiName:$wifiName")
//        val wifiName = "我是汉字".toString().asciiToHexString().upToNStrInBack(FIXED_LEN * 2, "f")
//        LogUtils.e("汉字:$wifiName")
        val wifiPwd = mBinding.etPwd.text.toString().asciiToHexString().upToNStrInBack(FIXED_LEN * 2, "f")

        val wifiInfoSb = StringBuilder()

        //wifi加密信息
        val tempStr = encryptDataStr((wifiName + wifiPwd), isNeedPadding = true, isUseRandomKey = isUseRandomKey)
        wifiInfoSb.append(tempStr)
        LogUtils.e("WiFiInfo:$wifiInfoSb")

        //IP地址
        val ipArray = mWiFiInfo.ip.split(".")
//        ipArray.forEach { wifiInfoSb.append(Integer.toHexString(it.toInt()).upToNStr(2).toUpperCase()) }


        //发送配网信息
        sendMsg(TCPInfo.APP_TYPE_WIFI_INFO,
                //如果isUseRandomKey为true 视为使用随机密钥
                if (isUseRandomKey) TCPInfo.CODE_SEND_WIFI_INFO_RANDOM_KEY else TCPInfo.CODE_SEND_WIFI_INFO_FIXED_KEY, wifiInfoSb.toString())
    }


    /**
     * 生成加密数据
     * @param srcString String
     * @param iv String? 偏移量 模式为CBC时候填入
     * @param isNeedPadding Boolean 是否需要补齐非16字节整数
     * @param isUseRandomKey Boolean 是否使用交换后的Key
     * @return String
     */
    private fun encryptDataStr(srcString: String, iv: String? = null, isNeedPadding: Boolean = false, isUseRandomKey: Boolean = false): String {
        //替换对应的加密key
        val encryptKey = if (isUseRandomKey) {
            LogUtils.e("使用randomKey")
            randomKey
        } else {
            LogUtils.e("使用fixedKey")
            if (aesBitType == VersionConstant.AESBitType.AES_128) {
                aes128Key
            } else {
                aes256Key
            }
        }

        var dataStr = srcString

        //非16字节整数倍补ff
        if (dataStr.length % 32 != 0 && isNeedPadding) {
            //补全后 应该的长度
            val paddingLen = ((dataStr.length / 32) + 1) * 32
            dataStr = dataStr.upToNStrInBack(paddingLen, "f")
        }


        //按模式加密数据
        return if (aesType == VersionConstant.AESType.AES_ECB) {
            if (isUseRandomKey) {
                AESCryptUtil.encryptECB(dataStr.hexStringToByteArray(), encryptKey.hexStringToByteArray())
            } else {
                AESCryptUtil.encryptECB(dataStr, encryptKey)
            }

        } else {
            if (iv.isNullOrBlank()) {
                LogUtils.e("ivSpec未传入,使用默认:$ivSpec")
                if (isUseRandomKey) {
                    AESCryptUtil.encryptCBC(dataStr.hexStringToByteArray(), encryptKey.hexStringToByteArray(), ivSpec.hexStringToByteArray())
                } else {
                    AESCryptUtil.encryptCBC(dataStr, encryptKey, ivSpec)
                }
            } else {
                if (isUseRandomKey) {
                    AESCryptUtil.encryptCBC(dataStr.hexStringToByteArray(), encryptKey.hexStringToByteArray(), ivSpec.hexStringToByteArray())
                } else {
                    AESCryptUtil.encryptCBC(dataStr, encryptKey, iv)
                }
            }
        }

    }


    /**
     * 发送数据包 自动组包头、包尾、长度、SN、校验值
     * @date 2020/12/23
     * @param opType String 命令类型
     * @param opCode String 命令号
     * @param data String 数据内容
     * @return
     */
    private fun sendMsg(opType: String, opCode: String, data: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            val sendString = getSendMsg(opType, opCode, data)
            mTcpClient?.send(sendString.replace(" ", "").hexStringToByteArray())
        }
    }

    private fun addDebugText(text: String) {
        val mTextView = mBinding.tvDebug
        if (StringUtils.isEmpty(text)) {
            mTextView.scrollTo(0, 0)
            mTextView.text = text
        } else {
            mTextView.append(text + "\n")
            mBinding.sv.fullScroll(View.FOCUS_DOWN)
        }
    }


    /**
     * 获取发送的数据包
     * @date 2020/12/31
     * @param opType String 命令类型
     * @param opCode String 命令号
     * @param data String 数据内容
     * @returno
     */
    private suspend fun getSendMsg(opType: String, opCode: String, data: String? = null): String = withContext(Dispatchers.IO) {
        sn = sn.inc()   //Kotlin自增返回数值 不改变源数据
        val snStr = sn.toString(16).upToNStr(2)
        val tempStr = StringBuilder()
                .append(opType) //命令类型
                .append(opCode) //命令号
                .append(snStr)  //SN

        //非版本校验/心跳包 带上版本号
        if (opType != TCPInfo.APP_TYPE_VERSION && opType != TCPInfo.APP_TYPE_HEARTBEAT) tempStr.append(versionByteArray.toHexStr(false))  //协议版本号

        if (data != null) tempStr.append(data)    //wifi信息

        val lenStr = Integer.toHexString((tempStr.length + 4) / 2).upToNStr(4)

        val dataStr = StringBuilder()
                .append(TCPInfo.FRAME_HEADER)   //帧头
                .append(lenStr) //长度
                .append(tempStr.toString())    //数据结构

        val crcNum = dataStr.toString().getCRC16()  //  CRC16/MODBUS 校验
        val sendStr = dataStr.append(crcNum).toString().addSpace()


        withContext(Dispatchers.Main) {
            addDebugText("<-- $sendStr")
        }

        LogUtils.e("<-- $sendStr\n" +
                "长度:$lenStr\n" +
                "命令类型:$opType\n" +
                "命令号:$opCode\n" +
                "SN:$snStr\n" +
                "原始数据:${data?.addSpace()}\n" +
                "校验:$crcNum")

        return@withContext sendStr
    }

    /**
     * 开始心跳包
     */
    private fun startHeartbeat() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000) //隔开1s再发送 避免黏包
            while (mTcpClient?.isConnected() == true) {
                sendMsg(TCPInfo.APP_TYPE_HEARTBEAT, TCPInfo.CODE_SEND_HEARTBEAT)
                delay(HEARTBEAT_TIME)
            }
        }
    }

}