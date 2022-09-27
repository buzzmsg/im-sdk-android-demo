package com.tmmtmm.demo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

/**
 * @description
 * @version
 */
class LoginViewModel(application: Application): AndroidViewModel(application) {

    fun login()= liveData(Dispatchers.IO) {
        emit("uid")
    }
}