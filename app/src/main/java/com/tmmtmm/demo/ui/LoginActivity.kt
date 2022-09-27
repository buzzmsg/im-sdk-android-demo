package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.databinding.ActivityLoginBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.demo.vm.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @description
 * @version
 */
class LoginActivity : BaseActivity() {

    private lateinit var mBinding: ActivityLoginBinding

    private val mViewModel by viewModels<LoginViewModel>()

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
        mBinding.btnLogin.click {
            login()
        }

    }

    override fun fetchData() {
    }

    private fun login(){
        mViewModel.login().observe(this){ uid ->

            loginSdk(uid)
        }
    }

    private fun loginSdk(uid: String){
        lifecycleScope.launch(Dispatchers.IO){
            TmLoginLogic.getInstance().login(uid, object : TmLoginLogic.LoginCallBack{
                override fun success() {

                }

                override fun fail(code: Int, errorMsg: String) {

                }

            })
        }
    }

}