package com.tmmtmm.demo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.tmmtmm.demo.api.*
import com.tmmtmm.demo.exception.TmException
import com.tmmtmm.demo.manager.LoginManager
import kotlinx.coroutines.Dispatchers

/**
 * @description
 * @version
 */
class LoginViewModel(application: Application): AndroidViewModel(application) {

    fun login1(phone: String)= liveData(Dispatchers.IO) {

        val loginResult = LoginByPhone.execute(LoginByPhoneRequest(phone = phone))
        val getAuthResult =
            GetAuth.execute(GetAuthRequest(LoginManager.INSTANCE.getToken()))

        if (loginResult !is ResponseResult.Success || getAuthResult !is ResponseResult.Success) {
            emit(ResponseResult.Failure(TmException.common()))
            return@liveData
        }
        val authCode = getAuthResult.value?.authcode ?: ""
        LoginManager.INSTANCE.setAuthCode(authCode)
        emit(loginResult)
    }


    fun login(phone: String)= liveData(Dispatchers.IO) {

        val result = LoginByPhone.execute(LoginByPhoneRequest(phone = phone))
        emit(result)
    }
}