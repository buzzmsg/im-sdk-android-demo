package com.tmmtmm.demo.ui.view

import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.ClickUtils
import com.tmmtmm.demo.R
import com.tmmtmm.demo.ui.ext.setDrawableLeft
import org.client.core.ui.layout.StateConstraintLayout
import org.client.core.ui.layout.StateLinearLayout
import org.client.core.ui.layout.StateToolBarConstraintLayout
import org.client.core.ui.layout.StateToolBarLinearLayout


class TitleBarView {

    var tvTopBarLeft: AppCompatTextView? = null

    var tvTopBarCenter: AppCompatTextView? = null

    var btnTopClose: AppCompatImageButton? = null

    var btnTopBarRight: AppCompatImageButton? = null

    var topBar: ConstraintLayout? = null

    var bottomLine: View? = null

    companion object {
        fun create(): TitleBarView {
            return TitleBarView()
        }
    }

    private fun initTitleBar(viewTitleVar: View) {
        topBar = viewTitleVar.findViewById(R.id.topBar)
        tvTopBarLeft = viewTitleVar.findViewById(R.id.tvTopBarLeft)
        tvTopBarCenter = viewTitleVar.findViewById(R.id.tvTopBarTitle)
        btnTopBarRight = viewTitleVar.findViewById(R.id.btnTopBarRight)
        btnTopClose = viewTitleVar.findViewById(R.id.btnTopBarLeftClose)
    }

    private fun View.generateDefaultTitleBar(title: String, leftBlock: (() -> Unit)? = null) {
        initTitleBar(this)
        tvTopBarCenter?.text = title
//        tvTopBarLeft?.setDrawableLeft(R.drawable.ic_left_dark_24_24)
        tvTopBarLeft?.setOnClickListener { leftBlock?.invoke() }
    }

    private fun View.generateCustomTitleBar(
        title: String? = "",
        leftRes: Int? = 0,
        leftText: String? = "",
        leftBlock: (() -> Unit)? = null,
        rightRes: Int? = 0,
        rightText: String? = "",
        rightBlock: (() -> Unit)? = null,
        resRightSecond: Int? = 0,
        rightSecondBlock: (() -> Unit)? = null,
    ) {
        initTitleBar(this)

        if (TextUtils.isEmpty(title)) {
            tvTopBarCenter?.visibility = View.GONE
        } else {
            tvTopBarCenter?.visibility = View.VISIBLE
            tvTopBarCenter?.text = title
        }

        if (leftRes != 0 || !TextUtils.isEmpty(leftText)) {
            tvTopBarLeft?.visibility = View.VISIBLE
            tvTopBarLeft?.setOnClickListener { leftBlock?.invoke() }
            if (!TextUtils.isEmpty(leftText)) {
                tvTopBarLeft?.text = leftText
            }

            if (leftRes != 0) {
                tvTopBarLeft?.setDrawableLeft(leftRes ?: 0)
            }
        } else {
            tvTopBarLeft?.visibility = View.GONE
        }


        if (resRightSecond != 0) {
            btnTopBarRight?.visibility = View.VISIBLE
            ClickUtils.applySingleDebouncing(btnTopBarRight, 1000) {
                rightSecondBlock?.invoke()
            }
//            btnTopBarRight?.setOnClickListener { rightSecondBlock?.invoke() }
            if (rightRes != 0) {
                btnTopBarRight?.setImageResource(resRightSecond ?: 0)
            }
        } else {
            btnTopBarRight?.visibility = View.GONE
        }
    }

    /************************      outer     ******************************************/


    fun showDefaultTitleBar(
        root: StateLinearLayout,
        title: String,
        leftBlock: (() -> Unit)? = null
    ) : TitleBarView {
        root.generateDefaultTitleBar(title, leftBlock)
        return this
    }

    fun showDefaultTitleBar(
        root: StateConstraintLayout,
        title: String,
        leftBlock: (() -> Unit)? = null
    ) : TitleBarView {
        root.generateDefaultTitleBar(title, leftBlock)
        return this
    }


    fun showTitleBar(
        lRoot: StateToolBarLinearLayout,
        title: String? = "",
        leftRes: Int? = 0,
        leftText: String? = "",
        leftBlock: (() -> Unit)? = null,
        rightRes: Int? = 0,
        rightText: String? = "",
        rightBlock: (() -> Unit)? = null,
        resRightSecond: Int? = 0,
        rightSecondBlock: (() -> Unit)? = null,
    ) : TitleBarView {
        lRoot.generateCustomTitleBar(
            title, leftRes, leftText, leftBlock, rightRes,
            rightText, rightBlock, resRightSecond, rightSecondBlock
        )
        return this
    }


    fun showTitleBar(
        cRoot: StateToolBarConstraintLayout,
        title: String? = "",
        leftRes: Int? = 0,
        leftText: String? = "",
        leftBlock: (() -> Unit)? = null,
        rightRes: Int? = 0,
        rightText: String? = "",
        rightBlock: (() -> Unit)? = null,
        resRightSecond: Int? = 0,
        rightSecondBlock: (() -> Unit)? = null,
    ) : TitleBarView {
        cRoot.generateCustomTitleBar(
            title, leftRes, leftText, leftBlock, rightRes,
            rightText, rightBlock, resRightSecond, rightSecondBlock
        )
        return this
    }
}
