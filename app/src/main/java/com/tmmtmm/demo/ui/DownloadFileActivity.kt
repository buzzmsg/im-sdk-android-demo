package com.tmmtmm.demo.ui

import com.tmmtmm.demo.base.BaseActivity


import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.im.sdk.view.mediapreview.view.DownloadFileView
import com.im.sdk.view.vo.StrategyVo
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityDownloadFileLayoutBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.ext.initToolbar
import com.tmmtmm.demo.utils.OpenFileUtil

class DownloadFileActivity : BaseActivity() {

    companion object {
        fun download(
            context: Context,
            amid: String? = null,
        ) {
            val intent = Intent(context, DownloadFileActivity::class.java)
            intent.putExtra("amid", amid)
            ActivityCompat.startActivity(context, intent, null)
        }


    }

    private var amid = ""
//    private var dialog: DialogFileOperate? = null

    private lateinit var binding: ActivityDownloadFileLayoutBinding


    override fun contentView() {
        binding = ActivityDownloadFileLayoutBinding.inflate(layoutInflater).bindView(this)
    }

    override fun initPrams() {
        amid = intent.getStringExtra("amid") ?: ""

        binding.toolbar.initToolbar(
            "",
            R.menu.menu_tool_bar_more,
            iconColor = R.color.black
        )
        binding.downFileView.removeAllViews()
//        val downFileView = TmApplication.instance().imSdk?.createDownloadFileView(amid, this)
//        binding.downFileView.addView(downFileView)
//        downFileView?.setMediaDelegate(object : DownloadFileView.MediaDelegate {
//            override fun onMediaPreview(strategyVo: StrategyVo) {
//                MediaPreviewActivity.newInstance(this@DownloadFileActivity, strategyVo)
//            }
//
//            override fun onOpenPdf(filePath: String, fileName: String, amid: String) {
//                ToastUtils.showShort("open pdf")
//            }
//
//            override fun onOpenApk(filePath: String) {
//                AppUtils.installApp(filePath)
//            }
//
//            override fun onOpenOtherFile(filePath: String) {
//                val intent = OpenFileUtil.openFile(filePath)
//                if (intent != null) {
//                    ActivityCompat.startActivity(this@DownloadFileActivity, intent, null)
//                }
//            }
//        })


//        binding.toolbar.setNavigationOnClickListener { finish() }
//        binding.toolbar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.add -> {
//                    if (dialog == null) {
//                        dialog = DialogFileOperate.newInstance(
//                            amid,
//                            downFileView?.filePath ?: "",
//                            downFileView?.fileName ?: "",
//                        )
//                    }
//                    if (dialog?.isVisible == true) return@setOnMenuItemClickListener true
//                    dialog?.showNow(supportFragmentManager, DialogFileOperate::class.java.name)
//                }
//            }
//            true
//        }
    }

    override fun initViews() {

    }

    override fun fetchData() {

    }


    override fun onDestroy() {
        super.onDestroy()
    }

}