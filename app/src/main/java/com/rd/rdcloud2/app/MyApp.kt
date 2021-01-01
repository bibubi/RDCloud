package com.rd.rdcloud2.app

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.rd.rdcloud2.BuildConfig
import com.rd.yunlib.utils.LogUtils as RDLogUtils


/**
 *
 * @author mR2hao
 * @date 2020/12/17
 */
class MyApp : Application() {
    /**
     * 是否Debug模式
     * @return Boolean
     */
    fun isDebug(): Boolean = BuildConfig.IS_LOG_ENABLE

    override fun onCreate() {
        super.onCreate()
//        YunManager.instance(this)
        //配置调试输出
        RDLogUtils.initLog(isDebug())
        LogUtils.getConfig().isLogSwitch = isDebug()
    }
}