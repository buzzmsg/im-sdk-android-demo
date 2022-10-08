package com.tmmtmm.sdk.ui.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ThreadUtils
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.tmmtmm.sdk.R
import com.tmmtmm.sdk.core.event.EventCenter
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.databinding.ConversationLayoutViewBinding
import com.tmmtmm.sdk.databinding.ItemConversationBinding
import com.tmmtmm.sdk.db.ConversationDbManager
import com.tmmtmm.sdk.db.UserDBManager
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.event.LoginSuccessEvent
import com.tmmtmm.sdk.dto.TmConversation
import com.tmmtmm.sdk.logic.TmConversationLogic
import com.tmmtmm.sdk.logic.TmMessageLogic
import com.tmmtmm.sdk.ui.view.conversation.ConversationView
import com.tmmtmm.sdk.ui.view.vo.TmmConversationVo
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @description
 * @version
 */
class TmConversationLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val KEY_LAST_MESSAGE = "lastMessage"
    private var mBinding: ConversationLayoutViewBinding

    private lateinit var mAdapter: ConversationAdapter
    private var helper: QuickAdapterHelper? = null

    private var mConversationEvent: EventCenter<ConversationEvent>? = null

    private val mutex = Mutex()

    private val TAG = "TmConversationLayout"

    init {
        val inflater = LayoutInflater.from(context)
        mBinding = ConversationLayoutViewBinding.inflate(inflater, this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mConversationEvent = ConversationDbManager.INSTANCE.addConversationCallback(null, object :
            ConversationEvent.ConversationListener {
            override fun onConversationChanged(data: ConversationEvent.EventData?) {
                Log.w(TAG, "onConversationChanged: 111111111111111111 chatIds = ${data?.chatIds}")
                updateConversation(data?.chatIds)
            }
        })

        UserDBManager.getInstance().addLoginSuccessCallback(null, object : LoginSuccessEvent.LoginSuccessListener {
            override fun onLoginSuccess(auid: String?) {
                request()
            }
        })

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mConversationEvent?.removeCallback()
        mConversationEvent = null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        mAdapter = ConversationAdapter()
        mBinding.conversationListView.layoutManager = LinearLayoutManager(context)
        mBinding.conversationListView.setHasFixedSize(true)
        mBinding.conversationListView.setItemViewCacheSize(0)


        helper = QuickAdapterHelper.Builder(mAdapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun onLoad() {
                    request()
                }

                override fun onFailRetry() {
                    request()
                }

                override fun isAllowLoading(): Boolean {
                    return true
                }
            }).build()

//        helper?.trailingLoadStateAdapter?.preloadSize = 1
        mBinding.conversationListView.adapter = helper?.adapter
        mBinding.conversationListView.itemAnimator = null

        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.items[position]
            itemClickCallBack?.onItemClick(item.aChatId)
        }

        request()
    }

    private fun request(){
        if (mAdapter.items.isEmpty()){
            TransferThreadPool.submitTask {
               val list = TmConversationLogic.INSTANCE.loadConversationList(Long.MAX_VALUE, 20)

                ThreadUtils.runOnUiThread {
                    setAdapterData(list)
                }
            }

            return
        }

        val lastIndex = mAdapter.items.lastIndex
        val lastConversation = mAdapter.getItem(lastIndex)
        val conversationList = TmConversationLogic.INSTANCE.loadConversationList(lastConversation?.dateUpdated ?: Long.MAX_VALUE, 20)

        ThreadUtils.runOnUiThread {
            if (conversationList.isEmpty()) {
                helper?.trailingLoadState = LoadState.NotLoading(true)
            }else {
                helper?.trailingLoadState = LoadState.NotLoading(false)
            }
            val currentConversationList =
                CopyOnWriteArrayList(mAdapter.items)
            val result = conversationList.minus(currentConversationList)
            mAdapter.addAll(result)
        }

    }

    private val lock = Any()
    private fun updateConversation(chatIds: MutableSet<String>?) {
        TransferThreadPool.submitTask {
            synchronized(lock) {
                var list =
                    TmConversationLogic.INSTANCE.getConversationCombination(chatIds)
                        ?: mutableListOf()

                val unReadMap =
                    TmMessageLogic.INSTANCE.getUnreadCount(chatIds?.toMutableList())

                val result: MutableList<TmmConversationVo>
                val currentConversationList =
                    CopyOnWriteArrayList(mAdapter.items.toMutableList())

                Log.w(TAG, "updateConversation: 333333333")

                if (list.isEmpty()) {
                    //conversation remove
                    list = currentConversationList.filter { tmmConversation ->
                        chatIds?.contains(tmmConversation.chatId) == true
                    }.toMutableList()
                    result =
                        currentConversationList.subtract(list.toSet()).toMutableList()
                } else {

                    val sorted = kotlin.Comparator<TmmConversationVo> { o1, o2 ->
                        if (o1.topTimeStamp > o2.topTimeStamp) {
                            -1
                        } else if (o1.topTimeStamp < o2.topTimeStamp) {
                            1
                        } else {
                            if (o1.dateUpdated > o2.dateUpdated) {
                                -1
                            } else if (o1.dateUpdated < o2.dateUpdated) {
                                1
                            } else {
                                1
                            }
                        }
                    }
                    //conversation add or update

                    if (list.size == chatIds?.size) {
                        result = list.union(currentConversationList).toMutableList()
                            .sortedWith(sorted) as MutableList<TmmConversationVo>
                    } else {
                        val listChatIds = list.map { it.chatId }.toMutableSet()
                        val needRemoveChatIds = chatIds?.subtract(listChatIds)


                        //conversation remove
                        val needRemoveList = currentConversationList.filter { tmmConversation ->
                            needRemoveChatIds?.contains(tmmConversation.chatId) == true
                        }.toMutableList()

                        val needAddList = list.subtract(needRemoveList.toSet()).toMutableList()

                        val updateList =
                            currentConversationList.subtract(needRemoveList.toSet()).toMutableList()

                        result = needAddList.union(updateList).toMutableList()
                            .sortedWith(sorted) as MutableList<TmmConversationVo>
                    }
                }

                for (tmmConversationVo in result) {
                    val unreadCount = unReadMap[tmmConversationVo.chatId]
                    if (unreadCount != null) {
                        tmmConversationVo.unReadCount = unreadCount
                    }
                }

                ThreadUtils.runOnUiThread {
//                TmLogUtils.getInstance()
//                    .i(
//                        content = "chatIds = $chatIds   lastEventMessage = ${
//                            GsonUtils.toJson(
//                                eventList.take(3)
//                            )
//                        }",
//                        printConsoleLog = false
//                    )
//
//                TmLogUtils.getInstance()
//                    .i(
//                        content = "chatIds = $chatIds   lastResultMessage = ${
//                            GsonUtils.toJson(
//                                resultList.take(3)
//                            )
//                        }",
//                        printConsoleLog = false
//                    )
                    Log.w(TAG, "updateConversation: 44444444444 $unReadMap")

                    setAdapterData(result.toMutableList())
                }
            }
        }

    }

    private fun setAdapterData(result: MutableList<TmmConversationVo>) {
        mAdapter.submitList(list = (result)) {
            if (!mBinding.conversationListView.canScrollVertically(-1)) {
                val state =
                    mBinding.conversationListView.layoutManager?.onSaveInstanceState()
                mBinding.conversationListView.layoutManager?.onRestoreInstanceState(state)
            }

        }
//        mAdapter?.setEmptyViewLayout(context, R.layout.view_conversation_empty)
//        mConversationAdapter.emptyLayout?.findViewById<View>(R.id.tvEmptyStartChat)?.click {
//            val type = SelectChatActivity.TYPE_INVITE_USER
//            SelectChatsUtils.getInstance()
//                .select(type = type)
//                .setLifeCycle(this)
//                .setSelectChatsCallBack(selectChatsCallBackWeakReference.get())
//                .setSelectChatsStrategy(CreateGroupChatStrategy())
//                .request()
//        }
//
//        mConversationAdapter.emptyLayout?.findViewById<View>(R.id.tvEmptyInviteFriends)?.click {
//            Launcher.navigation(IUserService::class.java)?.startPhoneContacts()
//        }
    }

    private var itemClickCallBack: ItemClickCallBack? = null

    fun setItemClickCallBack(callBack: ItemClickCallBack){
        this.itemClickCallBack = callBack
    }

    interface ItemClickCallBack {
        fun onItemClick(chatId: String)
    }


    private inner class ConversationAdapter() :
        BaseDifferAdapter<TmmConversationVo, ConversationAdapter.VH>(Differ()) {

        inner class VH(
            parent: ViewGroup,
            val viewBinding: ItemConversationBinding = ItemConversationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) : RecyclerView.ViewHolder(viewBinding.root)

        override fun onBindViewHolder(holder: VH, position: Int, item: TmmConversationVo?) {
            holder.viewBinding.conversation.setConversation(item)
        }

        override fun onBindViewHolder(
            holder: VH,
            position: Int,
            item: TmmConversationVo?,
            payloads: List<Any>
        ) {
            super.onBindViewHolder(holder, position, item, payloads)
            if (payloads.isEmpty()) {
                return
            }
            for (payload in payloads) {
                val bundle = payload as Bundle
                for (key in bundle.keySet()) {
                    when (key) {
                        KEY_LAST_MESSAGE -> {
                            val tmConversationVo =
                                bundle.getParcelable<TmmConversationVo>(KEY_LAST_MESSAGE)
                            holder.viewBinding.conversation.setLastMessage(tmConversationVo)
                        }

//                        KEY_UNREAD_COUNT -> {
//                            conversation.bindUnReadCount(data.chatId, data)
//                            conversation.bindAtMessage(data.chatId)
//                        }
//
////                        KEY_USER_INFO -> {
////                            conversation.updateConversationInfo(data.chatId)
////                        }
//
//                        KEY_IS_MUTE -> {
//                            val isMute = bundle.getBoolean(KEY_IS_MUTE, false)
//                            val muteText = holder.getView<TextView>(R.id.conversationItemMuteBtn)
//                            if (isMute) {
//                                muteText.text = context.resources.getString(R.string.string_un_mute)
//                                muteText.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                                    0,
//                                    R.drawable.ic_chat_unmute,
//                                    0,
//                                    0
//                                )
//                            } else {
//                                muteText.text = context.resources.getString(R.string.string_mute)
//                                muteText.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                                    0,
//                                    R.drawable.ic_chat_con_mute,
//                                    0,
//                                    0
//                                )
//                            }
//                            conversation.setMuteUnReadStatus(isMute)
//                        }
                    }
                }
            }
        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }
    }


    private inner class Differ : DiffUtil.ItemCallback<TmmConversationVo>() {
        override fun areItemsTheSame(
            oldItem: TmmConversationVo,
            newItem: TmmConversationVo
        ): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(
            oldItem: TmmConversationVo,
            newItem: TmmConversationVo
        ): Boolean {
            return (newItem.lastMid == oldItem.lastMid &&
                    newItem.dateUpdated == oldItem.dateUpdated &&
                    newItem.topTimeStamp == oldItem.topTimeStamp &&
                    newItem.lastTmmMessage?.status == oldItem.lastTmmMessage?.status)
        }

        override fun getChangePayload(
            oldItem: TmmConversationVo,
            newItem: TmmConversationVo
        ): Any? {
            Log.w(TAG, "getChangePayload: ===========" )
            val payload = Bundle()
            if (newItem.lastMid != oldItem.lastMid ||
                newItem.lastTmmMessage?.status != oldItem.lastTmmMessage?.status ||
                newItem.dateUpdated != oldItem.dateUpdated
            ) {

                Log.w(TAG, "getChangePayload: 1111111111" )
                payload.putParcelable(KEY_LAST_MESSAGE, newItem)
            }

            if (payload.size() == 0) {
                return null
            }
            return payload
        }
    }

}