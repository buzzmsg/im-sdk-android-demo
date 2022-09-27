package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.tmmtmm.demo.R
import com.tmmtmm.demo.api.ResponseResult
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.databinding.ActivityLoginBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.demo.ui.view.TitleBarView
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

        val titleBarView = TitleBarView()
        titleBarView.showTitleBar(
            cRoot = mBinding.root,
            title = "TmmTmm SDK",
        )

        mBinding.btnLogin.click {
            login()
        }

    }

    override fun fetchData() {
    }

    private fun login() {
        mViewModel.login(mBinding.etPhone.text.toString()).observe(this) { response ->


            if (response is ResponseResult.Success) {
                loginSdk("")
            }
        }
    }

    private fun loginSdk(uid: String) {
        TmLoginLogic.getInstance().login(uid, object : TmLoginLogic.LoginCallBack {
            override fun success() {
                MainActivity.newInstance(this@LoginActivity)
                finish()
            }

            override fun fail(code: Int, errorMsg: String) {

            }

        })
    }

}