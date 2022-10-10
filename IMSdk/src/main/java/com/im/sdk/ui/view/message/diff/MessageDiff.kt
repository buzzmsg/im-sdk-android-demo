package com.im.sdk.ui.view.message.diff

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.im.sdk.ui.view.vo.TmmMessageVo

/**
 * @description
 * @version
 */
class MessageDiff(
    private val oldData: MutableList<TmmMessageVo>?,
    private val newData: MutableList<TmmMessageVo>?,
) :
    DiffUtil.Callback() {

    companion object{
        const val KEY_MESSAGE_STATUS = "MessageStatus"
        const val KEY_MESSAGE_SEND_TIME = "SendTime"
    }

    override fun getOldListSize(): Int {
        return oldData?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newData?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: TmmMessageVo? = oldData?.get(oldItemPosition)
        val newItem: TmmMessageVo? = newData?.get(newItemPosition)
        return oldItem?.mid == newItem?.mid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem: TmmMessageVo? = oldData?.get(oldItemPosition)
        val newItem: TmmMessageVo? = newData?.get(newItemPosition)
        return ((newItem?.status == oldItem?.status)
                && (newItem?.sendTime == oldItem?.sendTime))
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem: TmmMessageVo? = oldData?.get(oldItemPosition)
        val newItem: TmmMessageVo? = newData?.get(newItemPosition)
        val payload = Bundle()
        if (newItem?.status != oldItem?.status) {
            payload.putInt(KEY_MESSAGE_STATUS, newItem?.status ?: 0)
        }

        if (newItem?.sendTime != oldItem?.sendTime) {
            payload.putLong(KEY_MESSAGE_SEND_TIME, newItem?.sendTime ?: 0)
        }

        if (payload.size() == 0) {
            return null
        }
        return payload
    }
}