package com.tmmtmm.demo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        } else {
            LoginActivity.newInstance(this)
        }
    }
}