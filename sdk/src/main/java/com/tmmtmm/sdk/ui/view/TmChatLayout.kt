package com.tmmtmm.sdk.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ThreadUtils
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.leading.LeadingLoadStateAdapter
import com.tmmtmm.sdk.core.event.EventCenter
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.databinding.ChatLayoutViewBinding
import com.tmmtmm.sdk.db.MessageDb
import com.tmmtmm.sdk.db.event.MessageEvent
import com.tmmtmm.sdk.logic.TmMessageLogic
import com.tmmtmm.sdk.ui.view.message.adapter.MessageAdapter
import com.tmmtmm.sdk.ui.view.message.diff.MessageDiff
import com.tmmtmm.sdk.ui.view.recyclerview.decoration.ChatStickyDecoration
import com.tmmtmm.sdk.ui.view.recyclerview.decoration.RvDecoration
import com.tmmtmm.sdk.ui.view.vo.TmmMessageVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

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

    private lateinit var mPowerfulStickyDecoration: ChatStickyDecoration

    private var chatId: String = ""

    private var messageEvent: EventCenter<MessageEvent>? = null

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ChatLayoutViewBinding.inflate(inflater, this, true)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        messageEvent =
            MessageDb.INSTANCE.addMessageCallback(null, object : MessageEvent.MessageListener {
                override fun onReceiveMessage(data: MessageEvent.EventData?) {
                    val dataMids = data?.mids ?: mutableSetOf()
                    if (dataMids.isEmpty()) return
                    if (data?.chatId == chatId) {

                        TransferThreadPool.submitTask {
                            val currentMessageList =
                                CopyOnWriteArrayList(mAdapter.items.toMutableList())

//                            val adapterMids =
//                                currentMessageList.map { it.mid ?: "" }.toMutableList()

                            val eventMessageList = TmMessageLogic.INSTANCE.getMessages(chatId, dataMids)

//                            val messageMaps = messageEventStrategy?.onReceiveMessages(
//                                chatId = chatId,
//                                adapterMids = adapterMids,
//                                eventMessageList = eventMessageList,
//                                viewModel = mChatMessageViewModel
//                            )
//
//                            viewModel.setChatIsRead(chatId)

                            val messageMaps = eventMessageList.associateBy(
                                { it.mid ?: "" },
                                { it }).toMutableMap()

                            ThreadUtils.runOnUiThread {
                                var result = mutableListOf<TmmMessageVo>()

                                var isLocalSend = false
                                if (messageMaps.isEmpty()) {
                                    //remove
                                    val list = currentMessageList.filter { tmmMessage ->
                                        data.mids?.contains(tmmMessage.mid) == true
                                    }.toMutableList()
                                    result = currentMessageList.subtract(list).toMutableList()
                                } else {
                                    //insert or update
                                    val receiveMessages = messageMaps.values
                                    val diffMessages =
                                        receiveMessages.minus(currentMessageList).toMutableList()

                                    for (diffMessage in diffMessages) {
                                        if (diffMessage.isOutMessage && diffMessage.isLocalSend == true) {
                                            isLocalSend = true
                                            break
                                        }
                                    }

                                    val needUpdateMessages =
                                        receiveMessages.intersect(currentMessageList)

                                    for (needUpdateMessage in needUpdateMessages) {
                                        val existIndex =
                                            currentMessageList.indexOf(needUpdateMessage)
                                        if (existIndex < 0) {
                                            continue
                                        }

                                        val element =
                                            messageMaps[needUpdateMessage.mid]
                                                ?: continue
                                        currentMessageList[existIndex] = element

                                        mAdapter.notifyItemChanged(existIndex)


                                    }

                                    result.addAll(currentMessageList)
                                    result.addAll(diffMessages)

//                                    markReceiveMessageBrowsed(diffMessages)

                                }
                                try {
//                                    updateBrowsedMessageId(result)

//                                    val diffResult =
//                                        DiffUtil.calculateDiff(
//                                            MessageDiff(
//                                                currentMessageList,
//                                                result
//                                            ),
//                                            true
//                                        )
                                    mAdapter.submitList(result)
//                                    diffResult.dispatchUpdatesTo(mAdapter)


                                    if (!mBinding.chatMessageListView.canScrollVertically(1) || isLocalSend) {
                                        val state =
                                            mBinding.chatMessageListView.layoutManager?.onSaveInstanceState()
                                        mBinding.chatMessageListView.layoutManager?.onRestoreInstanceState(
                                            state
                                        )

                                        mBinding.chatMessageListView.layoutManager?.scrollToPosition(
                                            mAdapter.itemCount - 1
                                        )


                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }




                    }


                }

            })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        messageEvent?.removeCallback()
        messageEvent = null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setChatListManager()


//        mBinding.chatMessageListView.layoutManager = LinearLayoutManager(context)

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

        mPowerfulStickyDecoration = RvDecoration.buildNewChatDecoration(
            context = context,
            onQuick = { position ->
                if (mAdapter.items.size > position && position >= 0) {
//                    Log.d(TAG, "setupMessageList() called with: position = $position data = ${mAdapter.items[position]}")
                    try {
                        mAdapter.items[position]
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            },
            isShowHistoryDecoration = { position ->
//                if (mAdapter.items.size > position) {
//
//                    val messageId = mBinding.notSeeMarkBtn.getNoSeeMessageId() ?: Long.MAX_VALUE
//
//                    val firstItem = adapter.items.elementAtOrNull(0)
//
//                    if (position == -1) {
//                        //can not find the last browseMessageIndex
//                        (mAdapter.items.size < 20 || !isHasMoreHistoryMessage) && firstItem is TmmMessageVo && messageId < firstItem.messageId
//                    } else {
//                        val item = adapter.items.get(position)
//                        if (item !is TmmMessageVo) {
//                            false
//                        } else {
//                            Log.w(
//                                TAG,
//                                "setupMessageList: ${mBinding.notSeeMarkBtn.getNoSeeMessageId()}"
//                            )
//                            item.messageId == messageId
//                        }
//                    }
//                } else {
//                    false
//                }
                false
            }
        )


        mBinding.chatMessageListView.addItemDecoration(mPowerfulStickyDecoration)
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
                if (mAdapter.items.isNotEmpty()) mAdapter.items.first() else null
            val list = TmMessageLogic.INSTANCE.loadMoreMessages(lastMessage?.messageId ?: 0, chatId)

            ThreadUtils.runOnUiThread {
                mAdapter.addAll(0, list)
                helper.leadingLoadState = LoadState.NotLoading(list.size < 20)

                forceScrollToPosition()
            }
        }
    }

    fun forceScrollToPosition() {
        if (mAdapter.itemCount > 0) {
            linearLayoutManager?.scrollToPositionWithOffset(mAdapter.itemCount - 1, 0)
        }
    }


}