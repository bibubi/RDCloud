package com.rd.rdcloud2.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.rd.rdcloud2.R
import com.rd.rdcloud2.databinding.ActivityMainBinding
import com.rd.yunlib.SERVER_URL
import com.rd.yunlib.YunManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {
    //数据绑定
    private lateinit var binding: ActivityMainBinding
    private lateinit var activity: Activity

    companion object {
        const val TAG = "Main"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }

    /**
     * 登录
     * @param view View
     */
    fun login(view: View) {
        GlobalScope.launch {
            val result = YunManager.instance(activity).login("222", "111111", 0)
            LogUtils.e(result.toString())
            withContext(Dispatchers.Main) {
                when {
                    result == null -> {
                        ToastUtils.showLong("登录失败 addr:${SERVER_URL}")
//                        activity.binding.root.showSnackBar("登录失败")
                    }
                    result.code != 200 -> {
                        ToastUtils.showLong("登录失败！code:${result.code},msg:${result.code}  ")
//                        activity.binding.root.showSnackBar("登录失败！code:${result.code},msg:${result.code}")
                    }
                    else -> {
                        ToastUtils.showLong("登录成功")
//                        activity.binding.root.showSnackBar("登录成功")
                    }
                }
            }
        }
    }

    fun test(view: View) {
        GlobalScope.launch {
            val result = YunManager.instance(activity).test()
            LogUtils.e(result)
        }

    }

    fun goTestTCP(view: View) {
        startActivity(getClearTopIntent(TCPTestActivity::class.java))
    }
}