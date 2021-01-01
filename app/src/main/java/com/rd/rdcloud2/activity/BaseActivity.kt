package com.rd.rdcloud2.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.rd.rdcloud2.app.BaseHandler

/**
 *
 * @author mR2hao
 * @date 2020/12/18
 */
open class BaseActivity : AppCompatActivity(), BaseHandler.BaseHandlerCallBack {
    lateinit var mHandler: BaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHandler = BaseHandler(this, this)
    }

    fun getClearTopIntent(clz: Class<*>): Intent {
        val intent = Intent(this, clz)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    /**
     * 检查权限
     * @param permission String 权限名
     * @return Boolean
     */
    fun checkPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求权限
     * @param permissions Array<String> 权限数组
     * @param reqCode Int
     */
    fun reqPermission(permissions: Array<String>, reqCode: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        requestPermissions(permissions, reqCode)
    }

    override fun callBack(msg: Message) {
    }
}