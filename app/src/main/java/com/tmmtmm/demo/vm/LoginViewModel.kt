package com.tmmtmm.demo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.tmmtmm.demo.api.LoginByPhone
import com.tmmtmm.demo.api.LoginByPhoneRequest
import kotlinx.coroutines.Dispatchers

/**
 * @description
 * @version
 */
class LoginViewModel(application: Application): AndroidViewModel(application) {

    fun login(phone: String)= liveData(Dispatchers.IO) {

        val result = LoginByPhone.execute(LoginByPhoneRequest(phone = phone))
        emit(result)
    }
}