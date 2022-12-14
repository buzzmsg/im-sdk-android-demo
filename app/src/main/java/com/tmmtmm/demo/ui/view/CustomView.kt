package com.tmmtmm.demo.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.ToastUtils
import com.tmmtmm.demo.databinding.ViewCustomBinding
import com.tmmtmm.demo.ui.ext.click

/**
 * @description
 * @version
 */
class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var mBinding: ViewCustomBinding

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ViewCustomBinding.inflate(inflater, this, true)
    }


    fun bindData(content: String) {
        mBinding.tvContent.text = content
        mBinding.tvContent.setOnClickListener {
            ToastUtils.showShort(content)
        }


        mBinding.test.setOnClickListener {
            ToastUtils.showShort("Test!!!")
        }

    }
}