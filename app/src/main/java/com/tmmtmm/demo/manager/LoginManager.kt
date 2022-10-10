package com.tmmtmm.demo.manager


import com.tmmtmm.demo.utils.SpUtils

/**
 * @description
 * @version
 */
class LoginManager private constructor() {

    companion object {

        const val LOGIN_UID = "uid"

        val INSTANCE: LoginManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LoginManager()
        }
    }

    fun setUserId(uid: String){
        SpUtils.putString("DEMO_UID",LOGIN_UID, uid)
    }

    fun getUserId(): String{
       return SpUtils.getString("DEMO_UID",LOGIN_UID)
    }

    fun setToken(token:String) {
        SpUtils.putString("DEMO_TOKEN",LOGIN_UID, token)
    }

    fun getToken() = SpUtils.getString("DEMO_TOKEN",LOGIN_UID)


    fun setAuthCode(authCode:String) {
        SpUtils.putString("DEMO_AuthCode",LOGIN_UID, authCode)
    }

    fun getAuthCode() = SpUtils.getString("DEMO_AuthCode",LOGIN_UID)


    fun isLogin(): Boolean{
        return getUserId().isNotBlank()
    }


//    suspend fun login(
//        phone: String,
//        prefix: String,
//        captcha: String
//    ): ResponseResult<UserLoginInfoDto?> {
//        return Login.post(phone = phone, prefix = prefix, captcha = captcha)
//    }


}