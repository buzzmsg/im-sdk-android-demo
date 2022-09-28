package com.tmmtmm.sdk.ui.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.tmmtmm.sdk.databinding.ConversationLayoutViewBinding
import com.tmmtmm.sdk.databinding.ItemConversationBinding
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

    val KEY_LAST_MESSAGE = "lastMessage"
    private var mBinding: ConversationLayoutViewBinding

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ConversationLayoutViewBinding.inflate(inflater, this)
    }


    inner class ConversationAdapter: BaseQuickAdapter<TmConversation,ConversationAdapter.VH>(){
        inner class VH(
            parent: ViewGroup,
            val viewBinding: ItemConversationBinding = ItemConversationBinding.inflate(
                LayoutInflater.from(parent.context), parent,
            )
        ) : RecyclerView.ViewHolder(viewBinding.root)

        override fun onBindViewHolder(holder: VH, position: Int, item: TmConversation?) {
//            holder.viewBinding.conversation.setConversation(data)
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }
    }


    inner class Differ : DiffUtil.ItemCallback<TmConversation>() {
        override fun areItemsTheSame(
            oldItem: TmConversation,
            newItem: TmConversation
        ): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(
            oldItem: TmConversation,
            newItem: TmConversation
        ): Boolean {
            return (newItem.lastMid == oldItem.lastMid &&
                    newItem.timestamp == oldItem.timestamp &&
                    newItem.topTimestamp == oldItem.topTimestamp &&
                    newItem.lastTmMessage?.status == oldItem.lastTmMessage?.status)
        }

        override fun getChangePayload(
            oldItem: TmConversation,
            newItem: TmConversation
        ): Any? {
            val payload = Bundle()
            if (newItem.lastMid != oldItem.lastMid ||
                newItem.lastTmMessage?.status != oldItem.lastTmMessage?.status ||
                newItem.timestamp != oldItem.timestamp
            ) {
                payload.putParcelable(KEY_LAST_MESSAGE, newItem)
            }


            if (payload.size() == 0) {
                return null
            }
            return payload
        }
    }

}