package com.im.sdk.ui.view.message

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.KeyboardUtils
import com.im.sdk.R
import com.im.sdk.databinding.SenderMessageItemViewBinding


class SenderMessageItemView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var messageContainerViewId = 0

    var array: TypedArray? = null

    private val TAG = "SenderMessageItemView"

    private var mBinding: SenderMessageItemViewBinding

//    init {
//        array =
//            context.obtainStyledAttributes(attrs, R.styleable.MessageItemView, defStyleAttr, 0)
//        messageContainerViewId =
//            array?.getResourceId(R.styleable.MessageItemView_messageContainerView, 0) ?: 0
//
//        Log.d(TAG, "getSenderMessageView start")
//        ChatCell.getSenderMessageView(this)
//        Log.d(TAG, "getSenderMessageView end")
//        try {
//
//
//            if (messageContainerViewId > 0) {
//
//                val childView =
//                    if (messageContainerViewId == R.layout.chat_message_text_item_view) {
//                        Log.d(TAG, "getTextCellView start")
//                        val view = ChatCell.getTextCellView(this)
//                        Log.d(TAG, "getTextCellView end")
//                        view
//                    } else {
//                        LayoutInflater.from(context).inflate(messageContainerViewId, this, false)
//                    }
//                find<ViewGroup>("messageContainerView")?.addView(childView)
//            }
//
//        } finally {
//            array?.recycle()
//        }
//        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//
//        layoutParams = params
//    }

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = SenderMessageItemViewBinding.inflate(inflater, this, false)

        val array =
            context.obtainStyledAttributes(attrs, R.styleable.MessageItemView, defStyleAttr, 0)
        try {
            val messageContainerViewId =
                array.getResourceId(R.styleable.MessageItemView_messageContainerView, 0)

            if (messageContainerViewId > 0) {
                val childView =
                    LayoutInflater.from(context).inflate(messageContainerViewId, this, false)
//                mBinding.messageContainerView.addView(childView)
            }

        } finally {
            array.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent() called with: event = $event")
        KeyboardUtils.hideSoftInput(context as AppCompatActivity)
        return super.onTouchEvent(event)
    }

}