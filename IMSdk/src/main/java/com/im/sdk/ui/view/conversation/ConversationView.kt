package com.im.sdk.ui.view.conversation

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.im.sdk.constant.MessageStatus
import com.im.sdk.core.event.EventCenter
import com.im.sdk.core.id.ChatId
import com.im.sdk.databinding.WidgetConversationViewBinding
import com.im.sdk.db.event.MessageEvent
import com.im.sdk.dto.TmConversation
import com.im.sdk.logic.TmLoginLogic
import com.im.sdk.time.ChatTime
import com.im.sdk.ui.view.vo.TmmConversationVo
import com.im.sdk.ui.view.vo.TmmMessageVo
import kotlinx.coroutines.sync.Mutex

/**
 * @description
 * @version
 */
class ConversationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mBinding: WidgetConversationViewBinding

    private var mTmmConversation: TmmConversationVo? = null

//    private var unReadNumEventCenter: EventCenter<UnReadNumEvent>? = null

//    private var userEventCenter: EventCenter<UserEvent>? = null

    private var messageEventCenter: EventCenter<MessageEvent>? = null

//    private var messageDraftEventCenter: EventCenter<MessageDraftEvent>? = null

    //    private var meetingEventCenter: EventCenter<MeetingEvent>? = null
//    private var meetingChangeEventCenter: EventCenter<MeetingChangeEvent>? = null
    private val mutex = Mutex()
//    private val TAG = "ConversationView"

    init {

        val inflater = LayoutInflater.from(context)
        mBinding = WidgetConversationViewBinding.inflate(inflater, this)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val mContext = context
        if (mContext !is AppCompatActivity) {
            return
        }

//        messageEventCenter = TmMessageLogic.getInstance()
//            .addMessageCallback(
//                mContext,
//                object : MessageEvent.MessageListener {
//                    override fun onReceiveMessage(data: MessageEvent.EventData?) {
//                        mTmmConversation?.let {
//                            if (data?.chatId != it.chatId) {
//                                return@let
//                            }
////                            bindLastMessageUpdateStatus(it)
//                        }
//                    }
//                })

//        messageDraftEventCenter = MessageDraftManager.getInstance()
//            .addMessageDraftCallback(
//                mContext,
//                object : MessageDraftListener {
//                    override fun onDraftMessageUpdate(data: MessageDraftEvent.EventData?) {
//                        mTmmConversation?.let {
//                            if (data?.chatId != it.chatId) {
//                                return@let
//                            }
//                            bindMessageDraftUpdate(it)
//                        }
//                    }
//                })
//
//        userEventCenter = TmUserManager.getInstance()
//            .addContactCallback(context as AppCompatActivity, object : ContactListener {
//                override fun onUpdateContact(data: UserEvent.EventData?) {
//                    mTmmConversation?.let {
//
//                        val mChatId = ChatId.createById(it.chatId ?: "")
//                        val isSingle = mChatId.isSingle()
//
//                        if (isSingle) {
//                            val targetUid = mChatId.getTargetId()
//                            if (data?.uids?.contains(targetUid) == true) {
//                                bindSingleConversationInfo()
//                            }
//                        } else {
//                            if (data?.uids?.contains(it.chatId) == true) {
//                                bindGroupConversationInfo()
//                            }
//
//                            if (data?.uids?.contains(it.lastTmmMessage?.uid) == true) {
//                                setLastMessage(it)
//                            }
//                        }
//                    }
//                }
//            })
//
//        meetingEventCenter = MeetingManager.getInstance()
//            .addMeetingCallback(context as AppCompatActivity,
//                object : MeetListener {
//                    override fun onUpdateMeeting(data: MeetingEvent.EventData?) {
//                        mTmmConversation?.let {
//                            if (data?.chatIds?.contains(it.chatId) == false) {
//                                return@let
//                            }
//                            bindMeetingStatus(it)
//                        }
//                    }
//                })
//        meetingChangeEventCenter = MeetingManager.getInstance()
//            .addMeetingChangeCallback(
//                context as AppCompatActivity,
//                object : MeetChangeListener {
//                    override fun onChangeMeeting(data: MeetingChangeEvent.EventData?) {
//                        mTmmConversation?.let {
//                            if (data?.chatId != it.chatId) {
//                                return@let
//                            }
//                            bindMeetingStatus(it)
//                        }
//                    }
//
//                })
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        messageEventCenter?.removeCallback()
        messageEventCenter = null

//        userEventCenter?.removeCallback()
//        userEventCenter = null
//        meetingEventCenter?.removeCallback()
//        meetingEventCenter = null
    }

    fun setConversation(tmmConversation: TmmConversationVo?) {
        mTmmConversation = tmmConversation

        setBackgroundColor(Color.TRANSPARENT)
//        if (tmmConversation.isStick) {
//            setBackgroundColor(ContextCompat.getColor(context, R.color.color_F2FBFC))
//        } else {
//            setBackgroundColor(Color.TRANSPARENT)
//        }

        setLastMessage(tmmConversation)
//        bindUnReadCount(tmmConversation.chatId, mTmmConversation)
//        bindAtMessage(tmmConversation.chatId)
//        bindMeetingStatus(tmmConversation)


//        val uid = tmmConversation?.uid ?: tmmConversation?.aChatId
//        mBinding.tvConversationName.text = tmmConversation?.name?.ifEmpty { uid?.substring(uid.length / 2, uid.length) }
        mBinding.tvConversationName.text = tmmConversation?.aChatId


        bindSingleConversationInfo()
//        bindConversationInfo()

//        if (tmmConversation.name.isEmpty()
//            || tmmConversation.name == tmmConversation.uid
//            || tmmConversation.name == tmmConversation.chatId
//        ) {
//            bindNameAndAvatar()
//        } else {
//            name.text = tmmConversation.name
//            avatar.load(tmmConversation.avatarUrl)
//                .originSize(
//                    tmmConversation.avatar?.width ?: 0,
//                    tmmConversation.avatar?.height ?: 0
//                )
//                .defaultStrategy(
//                    tmmConversation.avatar?.bucketId,
//                    tmmConversation.avatar?.width,
//                    tmmConversation.avatar?.height,
//                ).showImage()
//        }


//        val avatarInfo = mTmmConversation?.avatar
//        avatar.load(mTmmConversation?.avatarUrl)
//            .originSize(avatarInfo?.width ?: 0, avatarInfo?.height ?: 0)
//            .defaultStrategy(avatarInfo?.bucketId, avatarInfo?.width, avatarInfo?.height)
//            .showImage()
    }

    private fun setUpDateTime(dateUpdated: Long) {
        if (dateUpdated == 0L) {
            mBinding.tvDate.visibility = View.GONE
        } else {
            val showDate = ChatTime.getConversationTimeSpanString(
                dateUpdated
            )
            mBinding.tvDate.text = showDate
            mBinding.tvDate.visibility = View.VISIBLE
        }
    }

    fun setLastMessage(tmConversationVo: TmmConversationVo?) {
        val tmmMessage = tmConversationVo?.lastTmmMessage
//        val draftTmmMessage = tmConversationVo?.draftTmmMessage
        mTmmConversation?.isMute = tmConversationVo?.isMute ?: false
        mTmmConversation?.lastTmmMessage = tmmMessage
        mTmmConversation?.unReadCount = tmConversationVo?.unReadCount
        mTmmConversation?.lastMid = tmmMessage?.mid ?: ""
        if (tmConversationVo?.dateUpdated != null) {
            mTmmConversation?.dateUpdated = tmConversationVo.dateUpdated
        }

//        if (draftTmmMessage == null) {
        setMessageContent(tmmMessage, tmConversationVo)
//        } else {
//            setDraftMessageContent(draftTmmMessage)
//        }

        setLastMessageStatus(mTmmConversation)
    }

    private fun setMessageContent(
        tmmMessage: TmmMessageVo?,
        tmConversationVo: TmmConversationVo?
    ) {
        val lastMessageText = tmmMessage?.messageBody ?: ""
        setMessageText(lastMessageText ?: "")

//        if (tmmMessage?.type == MessageContentType.ContentType_Virtual_Currency_Pay) {
//            val tmPayContent = tmmMessage.tmmPayMessageContent
//            val isOut = tmPayContent?.fromId == TmLoginManager.getUserId()
//            message.text = when (tmPayContent?.act) {
//                BusinessDefine.BUSINESS_DEPOSIT -> {
//                    StringUtils.getString(R.string.string_wallet_receive)
//                }
//                in BusinessDefine.BUSINESS_TRANSFER -> {
//                    if (isOut) {
//                        StringUtils.getString(R.string.string_virtual_currency_transfer_result_success)
//                    } else {
//                        StringUtils.getString(R.string.string_wallet_receive)
//                    }
//                }
//
//                in BusinessDefine.BUSINESS_RED -> {
//                    if (tmPayContent?.act == BusinessDefine.BUSINESS_RED_PACKET_REFUND_USER
//                        || tmPayContent?.act == BusinessDefine.BUSINESS_RED_PACKET_REFUND_GROUP
//                    ) {
//                        StringUtils.getString(R.string.gift_refund)
//
//                    } else {
//                        if (isOut) {
//                            StringUtils.getString(R.string.string_virtual_currency_transfer_result_success)
//                        } else {
//                            StringUtils.getString(R.string.string_wallet_receive)
//                        }
//                    }
//                }
//
//                in BusinessDefine.BUSINESS_WITHDRAW -> {
//                    if (tmPayContent?.act == BusinessDefine.BUSINESS_WITHDRAW_REFUND
//                        || tmPayContent?.act == BusinessDefine.BUSINESS_WITHDRAW_SERVICE_CHARGE_REFUND
//                    ) {
//                        StringUtils.getString(R.string.withdraw_refund)
//                    } else {
//
//                        if (tmPayContent?.bizType == WithdrawStatusConstant.WITHDRAW_APPLY)
//                            StringUtils.getString(R.string.string_pay_withdraw_start)
//                        else StringUtils.getString(R.string.string_pay_withdraw_success)
//
//                    }
//                }
//                else -> {
//                    tmmMessage.messageType.convertText(resources, lastMessageText)
//                }
//            }
//
//            return
//        }

//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            mutex.withLock {
//                val conversationBody = lastMessageText
//                    when {
//
//
//                    tmmMessage?.type == MessageContentType.ContentType_At -> {
//                        getAttMessage(tmmMessage)
//                    }
//
//                    else -> {
//                        lastMessageText
//                    }
//                }

//                val mChatId = ChatId.createById(tmConversationVo?.chatId ?: "")

//                if (!mChatId.isSingle()
//                    && tmmMessage?.isCenterSender() == false
//                ) {
//
//                    val text: SpannableStringBuilder =
//                        when (tmConversationVo?.lastTmMessage?.sender) {
//                            TmLoginLogic.getInstance().getUserId() -> {
//                                //self
//
//
//                                SpanUtils().append("You:")
//                                    .setForegroundColor(R.color.text_5E6A81.getColor())
//                                    .appendSpace(4f.dpToPx())
//                                    .create()
//                            }
//                            else -> {
//                                //other user
//
//                                val uid = tmConversationVo?.lastTmMessage?.sender
//
////                        val name = if (tmConversationVo.lastTmmMessage?.name.isNullOrEmpty())
////                            uid?.substring(uid.length / 2, uid.length)
////                        else tmConversationVo.lastTmmMessage?.name
//
//                                val name = tmConversationVo?.lastTmMessage?.name
//
//
//                                if (!name.isNullOrEmpty()) {
//                                    SpanUtils().append("${name}:")
//                                        .setForegroundColor(R.color.text_5E6A81.getColor())
//                                        .appendSpace(4f.dpToPx())
//                                        .create()
//
//
//                                } else {
//
//                                    val userInfo = GroupMemberManager.getInstance()
//                                        .getGroupMemberInfo(
//                                            groupId = tmConversationVo?.chatId ?: "",
//                                            uid = tmConversationVo?.lastTmmMessage?.uid
//                                                ?: ""
//                                        )
//                                    tmConversationVo?.lastTmmMessage?.name =
//                                        if (userInfo.userName.isNullOrEmpty())
//                                            uid?.substring(uid.length / 2, uid.length)
//                                        else userInfo.userName
//
//
//                                    SpanUtils().append(
//                                        "${tmConversationVo?.lastTmmMessage?.name}:"
//                                    )
//                                        .setForegroundColor(R.color.text_5E6A81.getColor())
//                                        .appendSpace(4f.dpToPx())
//                                        .create()
//
//                                }
//
//                            }
//                        }
//
//                    withContext(Dispatchers.Main) {
//                        setMessageText(text.append(conversationBody ?: ""))
//                    }
//                    return@launch
//                }

//                withContext(Dispatchers.Main) {
//                    setMessageText(conversationBody ?: "")
//                }
//            }

    }


//    private fun setDraftMessageContent(
//        draftTmmMessage: TmmMessageVo?
//    ) {
//        val lastMessageText = draftTmmMessage?.messageBody ?: ""
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            mutex.withLock {
//                val conversationBody = when (draftTmmMessage?.messageType) {
//                    TmMessageContentType.ContentType_At -> {
//                        getAttMessage(draftTmmMessage)
//                    }
//                    else -> {
//                        draftTmmMessage?.messageType?.convertText(
//                            resources,
//                            lastMessageText
//                        )
//                    }
//                }
//
//
//                val text = if (conversationBody.isNullOrEmpty()) {
//                    SpanUtils().append("${context.getString(com.tmmtmm.im.style.R.string.chat_draft)}")
//                        .setForegroundColor(R.color.c_6844.getColor())
//                        .appendSpace(4f.dpToPx())
//                        .create()
//                } else {
//                    SpanUtils().append("${context.getString(com.tmmtmm.im.style.R.string.chat_draft)}:")
//                        .setForegroundColor(R.color.c_6844.getColor())
//                        .appendSpace(4f.dpToPx())
//                        .create()
//                }
//
//                withContext(Dispatchers.Main) {
//                    setMessageText(text.append(conversationBody ?: ""))
//                }
//            }
//        }
//    }

//    private fun getAttMessage(tmmMessage: TmmMessageVo): SpannableStringBuilder {
//        val atText =
//            TmAtMessageManager.getInstance().getAtMessage(
//                tmmMessage.chatId,
//                tmmMessage.tmmAtMessageContent?.items,
//                uidTextColor = R.color.text_message.getColor(),
//                textColor = R.color.text_message.getColor()
//            )
//
//        val builder = ExpressionUtils.getInstance().createEmoji(
//            SpannableStringBuilder(atText),
//            resources.getDimensionPixelSize(R.dimen.dp_16),
//            resources.getDimensionPixelSize(R.dimen.dp_16)
//        )
//        return builder
//    }

    private fun setMessageText(text: CharSequence?) {
        mBinding.tvMessage.text = text
    }

    private fun setLastMessageStatus(tmConversation: TmmConversationVo?) {
        val tmmMessage = tmConversation?.lastTmmMessage
        mTmmConversation?.isMute = tmConversation?.isMute ?: false
        Log.w(
            "TmConversationLayout",
            "setLastMessageStatus() called with: status = ${tmmMessage?.status}"
        )
        when (tmmMessage?.uid) {
            TmLoginLogic.getInstance().getUserId() -> {
                mBinding.messageStatus.setVisible()
                mBinding.messageStatus.showConversationStatus(tmConversation)
                when {
                    tmmMessage.status == com.im.sdk.constant.MessageStatus.Sending.value() -> {
                        mBinding.tvDate.text = "Sending..."
                    }
//                    else -> setUpDateTime(tmmMessage.displayTime)
                    else -> setUpDateTime(tmConversation.dateUpdated)

                }
            }
            else -> {
                mBinding.messageStatus.showConversationStatus(tmConversation)
                setUpDateTime(tmConversation?.dateUpdated ?: 0)
            }
        }
    }

    fun setMuteUnReadStatus(isMute: Boolean) {
        mTmmConversation?.isMute = isMute
        mBinding.messageStatus.showConversationStatus(mTmmConversation)
    }

    private fun bindGroupConversationInfo() {

//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val userInfo = ConversationManager.getConversationUserInfo(
//                mTmmConversation?.chatId,
//                mTmmConversation?.uid
//            )
//
//            val uid = mTmmConversation?.uid ?: mTmmConversation?.chatId
//            mTmmConversation?.name =
//                userInfo?.getShowName() ?: (uid?.substring(uid.length / 2, uid.length) ?: "")
//            mTmmConversation?.avatarUrl = userInfo?.avatar ?: ""
//            mTmmConversation?.avatar = userInfo?.avatarInfo
//            withContext(Dispatchers.Main) {
//                name.text = mTmmConversation?.name
//                avatar.load(mTmmConversation?.avatarUrl)
//                    .originSize(
//                        mTmmConversation?.avatar?.width ?: 0,
//                        mTmmConversation?.avatar?.height ?: 0
//                    )
//                    .defaultStrategy(
//                        mTmmConversation?.avatar?.bucketId,
//                        mTmmConversation?.avatar?.width,
//                        mTmmConversation?.avatar?.height
//                    )
//                    .showImage()
//            }
//        }
    }

    private fun bindConversationInfo() {
        val chatId = mTmmConversation?.chatId
        val createById = ChatId.createById(chatId ?: "")
//        if (createById.isSingle()) {
        bindSingleConversationInfo()
//        } else {
//            bindGroupConversationInfo()
//        }
    }

    private fun bindSingleConversationInfo() {
        mBinding.tvConversationName.text = mTmmConversation?.aChatId
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val userInfo = ConversationManager.getConversationUserInfo(
//                mTmmConversation?.chatId,
//                mTmmConversation?.uid
//            )
//
//            val uid = mTmmConversation?.uid ?: mTmmConversation?.chatId
//            mTmmConversation?.name =
//                userInfo?.getShowName() ?: (uid?.substring(uid.length / 2, uid.length) ?: "")
//            mTmmConversation?.avatarUrl = userInfo?.avatar ?: ""
//            mTmmConversation?.avatar = userInfo?.avatarInfo
//            withContext(Dispatchers.Main) {
//                name.text = mTmmConversation?.name
//                avatar.load(mTmmConversation?.avatarUrl)
//                    .originSize(
//                        mTmmConversation?.avatar?.width ?: 0,
//                        mTmmConversation?.avatar?.height ?: 0
//                    )
//                    .defaultStrategy(
//                        mTmmConversation?.avatar?.bucketId,
//                        mTmmConversation?.avatar?.width,
//                        mTmmConversation?.avatar?.height
//                    )
//                    .showImage()
//            }
//        }
    }


//    private fun bindMessageDraftUpdate(tmmConversation: TmConversation) {
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val draftMessage =
//                MessageDraftManager.getInstance()
//                    .getDraftMessage(tmmConversation.chatId)
//                    ?.transformToTmmMessage()
//
//            withContext(Dispatchers.Main) {
//                tmmConversation.draftTmmMessage = draftMessage
//                val displayTime = draftMessage?.displayTime ?: 0
//
//                tmmConversation.dateUpdated =
//                    if (displayTime > tmmConversation.dateUpdated) displayTime else tmmConversation.dateUpdated
//                setLastMessage(tmmConversation)
//            }
//        }
//    }

    private fun bindLastMessageUpdateStatus(tmmConversation: TmConversation) {
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val lastMessage =
//                TmMessageLogic.INSTANCE.queryMessagesByMid(tmmConversation.lastMid)
//
//            val status = lastMessage.status
//            withContext(Dispatchers.Main) {
//                tmmConversation.lastTmMessage?.status =
//                    status ?: MessageStatus.Sending
////                setLastMessageStatus(tmmConversation)
//            }
//        }
    }

//    fun bindUnReadCount(
//        chatId: String?,
//        tmmConversation: TmConversation?
//    ) {
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val count = MessageManager.getInstance().queryLocalUnReadByChatId(chatId)
//            Log.d("ConversationFragment", "bindUnReadCount() called $count")
//            mTmmConversation?.unReadCount = count
//            withContext(Dispatchers.Main) {
//                message_status.showConversationStatus(mTmmConversation)
//            }
//        }
//    }
//
//    fun bindAtMessage(chatId: String?) {
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val isHaveAt = TmAtMessageManager.getInstance().haveAt(chatId)
//
//            withContext(Dispatchers.Main) {
//                if (isHaveAt) message_at.visible()
//                else message_at.gone()
//            }
//        }
//    }
//
//    private fun bindMeetingStatus(tmmConversation: TmConversation?) {
//        (context as AppCompatActivity).lifecycleScope.launch(Dispatchers.IO) {
//            val tmmMeetingVo =
//                MeetingManager.getInstance().getMeetingStatus(tmmConversation?.chatId)
//            withContext(Dispatchers.Main) {
//                if (tmmMeetingVo == null) {
//                    voiceImage?.gone()
//                } else {
//                    voiceImage?.visible()
//                }
//            }
//        }
//    }
}