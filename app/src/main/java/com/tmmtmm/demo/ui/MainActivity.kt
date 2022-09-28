package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
//import com.chad.library.adapter.base.BaseBinderAdapter
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.databinding.ActivityMainBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.view.TitleBarView

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
//    private val mAdapter = BaseBinderAdapter()

    companion object {
        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        mBinding = ActivityMainBinding.inflate(layoutInflater).bindView(this)
    }

    override fun initPrams() {

    }

    override fun initViews() {
        val titleBarView = TitleBarView()
        titleBarView.showTitleBar(
            cRoot = mBinding.root,
            title = "聊天",
        )
    }

    override fun fetchData() {

    }




}