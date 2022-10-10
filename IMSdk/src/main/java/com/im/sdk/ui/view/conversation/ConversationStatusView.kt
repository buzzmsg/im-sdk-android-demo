package com.im.sdk.ui.view.conversation

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.im.sdk.R
import com.im.sdk.core.id.ChatId
import com.im.sdk.databinding.ViewConversationStatusBinding
import com.im.sdk.logic.TmLoginLogic
import com.im.sdk.ui.ext.gone
import com.im.sdk.ui.ext.visible
import com.im.sdk.ui.view.vo.TmmConversationVo

/**
 * @description
 * @time 2022/1/11
 * @version
 */
class ConversationStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var mBinding: ViewConversationStatusBinding


    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ViewConversationStatusBinding.inflate(inflater, this)
    }

    fun showConversationStatus(tmmConversationVo: TmmConversationVo?) {
        val isMute = tmmConversationVo?.isMute ?: false
        val tmmMessage = tmmConversationVo?.lastTmmMessage
        val isSingle = ChatId.createById(tmmMessage?.chatId ?: "").isSingle()
        val unReadCount = tmmConversationVo?.unReadCount ?: 0
        Log.d(
            "ConversationFragment",
            "showConversationStatus() called with: unReadCount = $unReadCount"
        )
        if (tmmMessage == null) {
            mBinding.ivConversationStatus.gone()
            mBinding.tvConversationUnReadCount.gone()
            return
        }
        when {
//            tmmMessage.isRead() -> {
//                mBinding.ivConversationStatus.hideLoading()
//
//                if (isMute) {
//                    mBinding.ivConversationStatus.setImageResource(R.drawable.ic_chat_mute)
//                    if (unReadCount > 0) {
//                        mBinding.tvConversationUnReadCount.setBackgroundResource(R.drawable.shape_round_rect_mute_bg_message_count)
//                        mBinding.ivConversationStatus.gone()
//                        mBinding.tvConversationUnReadCount.visible()
//                        mBinding.tvConversationUnReadCount.text =
//                            when {
//                                unReadCount >= 99 -> {
//                                    "  99+  "
//                                }
//                                unReadCount > 9 -> {
//                                    "  $unReadCount  "
//                                }
//                                else -> {
//                                    unReadCount.toString()
//                                }
//                            }
//                    } else {
//                        mBinding.tvConversationUnReadCount.gone()
//                        mBinding.ivConversationStatus.visible()
//                    }
//                } else {
//                    if (unReadCount > 0) {
//                        mBinding.tvConversationUnReadCount.setBackgroundResource(R.drawable.shape_round_rect_bg_message_count)
//                        mBinding.ivConversationStatus.gone()
//                        mBinding.tvConversationUnReadCount.visible()
//                        mBinding.tvConversationUnReadCount.text =
//                            when {
//                                unReadCount >= 99 -> {
//                                    "  99+  "
//                                }
//                                unReadCount > 9 -> {
//                                    "  $unReadCount  "
//                                }
//                                else -> {
//                                    unReadCount.toString()
//                                }
//                            }
//
//                    } else if (tmmMessage.isOutMessage) {
//                        mBinding.tvConversationUnReadCount.gone()
//                        if (isSingle) {
//                            mBinding.ivConversationStatus.setImageResource(R.drawable.ic_conversation_message_readed)
//                        } else {
//                            mBinding.ivConversationStatus.setImageResource(R.drawable.ic_conversation_message_received)
//                        }
//                        mBinding.ivConversationStatus.visible()
//                    } else {
//                        mBinding.ivConversationStatus.gone()
//                        mBinding.tvConversationUnReadCount.gone()
//                    }
//                }
//
//            }
            tmmMessage.status == com.im.sdk.constant.MessageStatus.Sent.value() -> {
                mBinding.ivConversationStatus.hideLoading()
//                if (isMute) {
//                    if (unReadCount > 0) {
//                        mBinding.ivConversationStatus.gone()
//                        mBinding.tvConversationUnReadCount.setBackgroundResource(R.drawable.shape_round_rect_mute_bg_message_count)
//                        mBinding.tvConversationUnReadCount.visible()
//                        mBinding.tvConversationUnReadCount.text =
//                            when {
//                                unReadCount >= 99 -> {
//                                    "  99+  "
//                                }
//                                unReadCount > 9 -> {
//                                    "  $unReadCount  "
//                                }
//                                else -> {
//                                    unReadCount.toString()
//                                }
//                            }
//                    } else {
//                        mBinding.ivConversationStatus.setImageResource(R.drawable.ic_chat_mute)
//                        mBinding.tvConversationUnReadCount.gone()
//                        mBinding.ivConversationStatus.visible()
//                    }
//                } else {
//                    if (unReadCount > 0) {
//                        mBinding.tvConversationUnReadCount.setBackgroundResource(R.drawable.shape_round_rect_bg_message_count)
//
//                        mBinding.ivConversationStatus.gone()
//                        mBinding.tvConversationUnReadCount.visible()
//                        mBinding.tvConversationUnReadCount.text =
//                            when {
//                                unReadCount >= 99 -> {
//                                    "  99+  "
//                                }
//                                unReadCount > 9 -> {
//                                    "  $unReadCount  "
//                                }
//                                else -> {
//                                    unReadCount.toString()
//                                }
//                            }
//
//                    } else
                if (tmmMessage.uid == TmLoginLogic.getInstance().getUserId()) {
                    mBinding.tvConversationUnReadCount.gone()
                    mBinding.ivConversationStatus.setImageResource(R.drawable.ic_conversation_message_received)
                    mBinding.ivConversationStatus.visible()
                } else {
                    mBinding.ivConversationStatus.gone()
                    mBinding.tvConversationUnReadCount.gone()
                }
//                }
            }
            tmmMessage.status == com.im.sdk.constant.MessageStatus.Send_Failure.value() -> {
                mBinding.ivConversationStatus.hideLoading()
                mBinding.ivConversationStatus.visible()
                mBinding.tvConversationUnReadCount.gone()
//                if (!isSingle) {
//                    mBinding.ivConversationStatus.setImageResource(R.drawable.ic_chat_mute)
//                } else {
                mBinding.ivConversationStatus.setImageResource(R.drawable.ic_conversation_message_error)
//                }
//                if (isMute && !isSingle) {
//                    mBinding.ivConversationStatus.setImageResource(R.drawable.ic_chat_mute)
//                } else {
//                    mBinding.ivConversationStatus.setImageResource(R.drawable.ic_conversation_message_error)
//                }
            }
            else -> {
                mBinding.ivConversationStatus.visible()
                mBinding.ivConversationStatus.showLoading()
                mBinding.tvConversationUnReadCount.gone()
            }
        }
    }

    fun setVisible() {
        visible()
    }

}