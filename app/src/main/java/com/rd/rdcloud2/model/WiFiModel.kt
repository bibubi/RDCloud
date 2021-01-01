package com.rd.rdcloud2.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 * @author mR2hao
 * @date 2020/12/28
 */
class WiFiModel : ViewModel() {
    val wifiName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val wifiIp: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


}