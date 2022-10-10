package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.tmmtmm.demo.api.ResponseResult
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityLoginBinding
import com.tmmtmm.demo.manager.LoginManager
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.demo.ui.view.TitleBarView
import com.tmmtmm.demo.vm.LoginViewModel

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
        showLoading()
        mViewModel.login(mBinding.etPhone.text.toString()).observe(this) { response ->
            hideLoading()
            if (response is ResponseResult.Success) {
                val value = response.value
                if (value?.auid.isNullOrBlank()) {
                    return@observe
                }
                LoginManager.INSTANCE.setUserPhone(mBinding.etPhone.text.toString())
                LoginManager.INSTANCE.setUserId(value?.auid ?: "")
                LoginManager.INSTANCE.setToken(value?.token ?: "")
                TmApplication.instance().imSdk?.initUser(value?.auid ?: "")
                MainActivity.newInstance(this@LoginActivity)
                finish()
            }
        }
    }
}