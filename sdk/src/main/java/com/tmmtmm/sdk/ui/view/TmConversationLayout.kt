package com.tmmtmm.sdk.ui.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import com.tmmtmm.sdk.databinding.ConversationLayoutViewBinding
import com.tmmtmm.sdk.dto.TmConversation

/**
 * @description
 * @version
 */
class TmConversationLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private var mBinding: ConversationLayoutViewBinding


    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ConversationLayoutViewBinding.inflate(inflater, this)
    }



//    inner class Differ : DiffUtil.ItemCallback<TmConversation>() {
//        override fun areItemsTheSame(
//            oldItem: TmConversation,
//            newItem: TmConversation
//        ): Boolean {
//            return oldItem.chatId == newItem.chatId
//        }
//
//        override fun areContentsTheSame(
//            oldItem: TmConversation,
//            newItem: TmConversation
//        ): Boolean {
//            return (newItem.lastMid == oldItem.lastMid &&
//                    newItem.dateUpdated == oldItem.dateUpdated &&
//                    newItem.topTimeStamp == oldItem.topTimeStamp &&
//                    newItem.lastTmmMessage?.status == oldItem.lastTmmMessage?.status)
//        }
//
//        override fun getChangePayload(
//            oldItem: TmConversation,
//            newItem: TmConversation
//        ): Any? {
//            val payload = Bundle()
//            if (newItem.lastMid != oldItem.lastMid ||
//                newItem.lastTmmMessage?.status != oldItem.lastTmmMessage?.status ||
//                newItem.dateUpdated != oldItem.dateUpdated
//            ) {
//                payload.putParcelable(KEY_LAST_MESSAGE, newItem)
//            }
//
////                if (newItem.dateUpdated != oldItem.dateUpdated) {
////                    payload.putLong(KEY_DATE_UPDATED, newItem.dateUpdated)
////                }
//
//            if (payload.size() == 0) {
//                return null
//            }
//            return payload
//        }
//    }

}