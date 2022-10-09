package com.tmmtmm.demo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.manager.LoginManager


/**
 * @description
 * @version
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (LoginManager.INSTANCE.isLogin()) {
            MainActivity.newInstance(this)
            val userId = LoginManager.INSTANCE.getUserId()
            TmApplication.instance().imSdk?.initUser(userId)
        } else {
            LoginActivity.newInstance(this)
        }
        finish()
    }
}