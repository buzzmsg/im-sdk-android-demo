package com.tmmtmm.demo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView

/**
 * @description
 * @version
 */
abstract class BaseActivity : AppCompatActivity() {

    var loadingPopup: LoadingPopupView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarLightMode(window,true)
        contentView()
        initPrams()
        initViews()
        fetchData()
    }

    fun showLoading() {
        if (loadingPopup == null) {
            loadingPopup = XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .isLightNavigationBar(true) //                            .asLoading(null, R.layout.custom_loading_popup)
                .asLoading("", LoadingPopupView.Style.ProgressBar)
                .show() as LoadingPopupView
        }
    }

    fun hideLoading() {
        loadingPopup?.dismiss()
    }

    abstract fun contentView()

    abstract fun initPrams()

    abstract fun initViews()

    abstract fun fetchData()
}