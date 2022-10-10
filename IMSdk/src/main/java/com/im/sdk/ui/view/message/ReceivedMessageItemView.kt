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
import com.im.sdk.databinding.ReceivedMessageItemViewBinding

class ReceivedMessageItemView @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var messageContainerViewId = 0

    var array: TypedArray? = null

    private val TAG = "ReceivedMessageItemView"

    private var mBinding: ReceivedMessageItemViewBinding

//    init {
//        array =
//            context.obtainStyledAttributes(attrs, R.styleable.MessageItemView, defStyleAttr, 0)
//        messageContainerViewId =
//            array?.getResourceId(R.styleable.MessageItemView_messageContainerView, 0) ?: 0
//        ChatCell.getReceiveMessageView(this)
//
////        LayoutInflater.from(context).inflate(R.layout.received_message_item_view, this, true)
//
//        try {
//
//            if (messageContainerViewId > 0) {
//
//                val childView = if (messageContainerViewId == R.layout.chat_message_text_item_view) {
//                    val view = ChatCell.getTextCellView(this)
//                    view
//                } else {
//                    LayoutInflater.from(context).inflate(messageContainerViewId, this, false)
//                }
//                find<ViewGroup>("messageContainerView")?.addView(childView)
//            }
//
//        } finally {
//            array?.recycle()
//        }
//    }

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ReceivedMessageItemViewBinding.inflate(inflater, this, false)

        val array =
            context.obtainStyledAttributes(attrs, R.styleable.MessageItemView, defStyleAttr, 0)
        try {
            val messageContainerViewId = array.getResourceId(
                R.styleable.MessageItemView_messageContainerView,
                0
            )

            if (messageContainerViewId > 0) {


//            View view = cellViewCreator.getView(viewGroup);
//            if (view == null) {
//                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(cellViewCreator.getItemViewId(), viewGroup, false);
//                val childView  = X2C.inflate(
//                    context,
//                    messageContainerViewId,
//                    this,
//                    false
//                )


                val childView =
                    LayoutInflater.from(context).inflate(messageContainerViewId, this, false)
//                try{
//                    val bottomTextView = childView.findViewById<View>(R.id.tvMessageRedPacketStatus)
//                    val currencyTextView = childView.findViewById<View>(R.id.tvVirtualCurrencyTransferAmount)
//                    val bottomTextView1 = childView.findViewById<View>(R.id.descView)
//                    if(bottomTextView!=null){
//                        val layoutParams:ConstraintLayout.LayoutParams = bottomTextView.layoutParams as LayoutParams
//                        layoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.dp_16)
//                        bottomTextView.layoutParams=layoutParams
//                    }
//                    if(currencyTextView!=null&&bottomTextView1!=null){
//                        val layoutParams:ConstraintLayout.LayoutParams = bottomTextView1.layoutParams as LayoutParams
//                        layoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.dp_16)
//                        bottomTextView1.layoutParams=layoutParams
//                    }
//                }catch (e:Exception){}
//                mBinding.messageContainerView.addView(childView)
            }

        } finally {
            array.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent() called with: event = $event")
        KeyboardUtils.hideSoftInput(context as AppCompatActivity)
        return super.onTouchEvent(event)
    }
}