package com.tmmtmm.sdk.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ThreadUtils
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.leading.LeadingLoadStateAdapter
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.databinding.ChatLayoutViewBinding
import com.tmmtmm.sdk.logic.TmMessageLogic
import com.tmmtmm.sdk.ui.view.message.adapter.MessageAdapter
import com.tmmtmm.sdk.ui.view.vo.TmmMessageVo

/**
 * @description
 * @version
 */
class TmChatLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener {

    private val mBinding: ChatLayoutViewBinding


    private var linearLayoutManager: LinearLayoutManager? = null
    private val mAdapter = MessageAdapter()
    private lateinit var helper: QuickAdapterHelper

    private var chatId: String = ""

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ChatLayoutViewBinding.inflate(inflater, this, true)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        setChatListManager()


        mBinding.chatMessageListView.layoutManager = LinearLayoutManager(context)

        helper = QuickAdapterHelper.Builder(mAdapter)
            .setLeadingLoadStateAdapter(object : LeadingLoadStateAdapter.OnLeadingListener {
                override fun onLoad() {
                    loadMoreMessages()
                }

                override fun isAllowLoading(): Boolean {
                    return true
                }
            }).build()

        mBinding.chatMessageListView.adapter = helper.adapter
    }

    fun createChat(aChatId: String) {
        chatId = ChatId.create(aChatId)
        loadMessage()
    }

    private fun setChatListManager() {
        linearLayoutManager = LinearLayoutManager(context)
        mBinding.chatMessageListView.layoutManager = linearLayoutManager
//        mBinding.chatMessageListView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {

    }

    private fun loadMessage() {

        if (chatId.isBlank()) return


        TransferThreadPool.submitTask {
            val list = TmMessageLogic.INSTANCE.loadMessage(Long.MAX_VALUE, chatId)

            ThreadUtils.runOnUiThread {
                mAdapter.submitList(list)
                helper.leadingLoadState = LoadState.NotLoading(list.size < 20)

                forceScrollToPosition()
            }
        }
    }


    private fun loadMoreMessages() {

        if (chatId.isBlank()) return

        helper.leadingLoadState = LoadState.Loading
        TransferThreadPool.submitTask {
            val lastMessage =
                if (mAdapter.items.isNotEmpty()) mAdapter.items.last() else null
            val list = TmMessageLogic.INSTANCE.loadMoreMessages(lastMessage?.messageId ?: 0, chatId)

            ThreadUtils.runOnUiThread {
                mAdapter.addAll(0,list)
                helper.leadingLoadState = LoadState.NotLoading(list.size < 20)

                forceScrollToPosition()
            }
        }
    }

    private fun forceScrollToPosition() {
        if (mAdapter.itemCount > 0) {
            linearLayoutManager?.scrollToPositionWithOffset(mAdapter.itemCount - 1, 0)
        }
    }


}