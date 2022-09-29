package com.tmmtmm.sdk.ui.view.message

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.tmmtmm.sdk.R
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.ui.ext.gone
import com.tmmtmm.sdk.ui.ext.visible


/**
 * @description
 * @time 2022/1/11
 * @version
 */
class MessageSendingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var animator: ObjectAnimator? = null


    fun showMessageStatus(status: Int) {
        when (status) {
            MessageStatus.Readed.value() -> {
                animator?.end()
//                if (isSingle) {
//                    setImageResource(R.drawable.ic_conversation_message_readed)
//                } else {
                    setImageResource(R.drawable.ic_conversation_message_received)
//                }
            }
            MessageStatus.Sent.value() -> {
                animator?.end()
                setImageResource(R.drawable.ic_conversation_message_received)
            }
            MessageStatus.Sending.value() -> {
                showLoading()
            }
            else -> {
                animator?.end()
                setImageResource(R.drawable.ic_conversation_message_error)
            }
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
    }

    fun setGone() {
        animator?.cancel()
        gone()
    }

    fun setVisible() {
        visible()
    }

    fun hideLoading() {
        animator?.end()
    }

    fun showLoading() {
        if (animator?.isRunning == true) {
            return
        }
        setImageResource(R.drawable.ic_chat_send_loading)
        pivotX = layoutParams.height.div(2).toFloat()
        pivotY = layoutParams.height.div(2).toFloat()
        animator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f)
        animator?.duration = 1200
        //AccelerateInterpolator()
        animator?.interpolator = LinearInterpolator();
        animator?.repeatCount = ObjectAnimator.INFINITE;
        animator?.start()
    }
}