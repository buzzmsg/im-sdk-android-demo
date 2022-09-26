package com.tmmtmm.sdk.ui

import android.content.Context
import android.content.Intent
import com.tmmtmm.sdk.base.BaseActivity
import com.tmmtmm.sdk.databinding.ActivityLoginBinding
import com.tmmtmm.sdk.ui.ext.bindView

/**
 * @description
 * @version
 */
class LoginActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLoginBinding
    companion object {
        fun newInstance(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        mBinding = ActivityLoginBinding.inflate(layoutInflater).bindView(this)
    }

    override fun initPrams() {

    }

    override fun initViews() {

    }

    override fun fetchData() {

    }

}