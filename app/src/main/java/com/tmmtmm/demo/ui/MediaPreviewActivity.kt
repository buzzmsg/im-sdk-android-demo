package com.tmmtmm.demo.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.im.sdk.view.ext.*
import com.im.sdk.view.mediapreview.dialog.IMDialogMediaOperate
import com.im.sdk.view.mediapreview.model.ActionFinishCallback
import com.im.sdk.view.mediapreview.model.MediaActionImpl
import com.im.sdk.view.mediapreview.model.MediaActionVo
import com.im.sdk.view.mediapreview.view.PagerTransitionView
import com.im.sdk.view.vo.IMMediaMoreActionVo
import com.im.sdk.view.vo.MediaPreviewType
import com.im.sdk.view.vo.StrategyVo
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityMediaPreviewBinding
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.filepicker.utils.bindView
import org.client.core.ui.statusbar.AndroidStatusBarUtils


/**
 * @description
 * @time 2022/4/12
 * @version
 */
class MediaPreviewActivity : AppCompatActivity() {


    private var mediaPreview: PagerTransitionView? = null
    private var dialog: IMDialogMediaOperate? = null
    private var strategyVo: StrategyVo? = null



    companion object {

        fun newInstance(context: Context, strategyVo: StrategyVo) {
            val intent = Intent()
            intent.setClass(context, MediaPreviewActivity::class.java)
            intent.putExtra("strategyVo", strategyVo)
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private lateinit var mBinding: ActivityMediaPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        mBinding = ActivityMediaPreviewBinding.inflate(layoutInflater).bindView(this)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        intent?.apply {
            strategyVo = this.getParcelableExtra("strategyVo")
        }
        mediaPreview =
            TmApplication.instance().imSdk?.createMediaPreview(this, this, strategyVo) {
                backgroundEndColor = R.color.black.getColor()
                isOpenAnimation = true
            }

        mBinding.mediaPreview.removeAllViews()
        if (mediaPreview != null) {
            mBinding.mediaPreview.addView(mediaPreview)
        }

        mBinding.topSettingBar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)

        setPreviewDelegate()
        onClick()
    }


    private fun onClick() {

        mBinding.imgCloseButton.click {
            finish()
        }

    }

    private fun setPreviewDelegate() {
//        mediaPreview?.setPreviewDelegate(object : PagerTransitionView.PreviewDelegate {
//            override fun finish() {
//                mediaPreview = null
//                this@MediaPreviewActivity.finish()
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//            }
//
//            override fun syncOriginalDownLoadStatus() {
//            }
//
//            override fun dragValue(dragValue: Float) {
//                allBtnShowType(dragValue)
//            }
//
//            override fun itemLongClickListener() {
//
//            }
//
//            override fun itemClickListener() {
//                switchViewShowType()
//            }
//
//            override fun indicatorData(currentIndex: Int, totalCount: Int) {
//                mBinding.tvIndicator.text =
//                    String.format("%s%s%s", "$currentIndex", "/", "$totalCount")
//            }
//        })
    }




    private fun allBtnShowType(dragValue: Float) {
        if (dragValue == 0F) {
            switchViewShowType(View.VISIBLE)
        } else {
            switchViewShowType(View.INVISIBLE)
        }
    }

    private fun switchViewShowType(visibility: Int? = null) {
        visibility?.let {
            mBinding.layoutTopBox.visibility = it
            mBinding.tvIndicator.visibility = it
            mBinding.imgCloseButton.visibility = it
        } ?: run {
            if (mBinding.layoutTopBox.visibility == View.VISIBLE) {
                switchViewShowType(View.INVISIBLE)
            } else {
                switchViewShowType(View.VISIBLE)
            }
        }
    }


    override fun finish() {
        mediaPreview?.let {
            it.finishView {
                clearData()
                super.finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        } ?: run {
            clearData()
            super.finish()
        }
    }

    private fun clearData() {
        mediaPreview = null
        dialog?.dismiss()
        dialog = null
    }

}