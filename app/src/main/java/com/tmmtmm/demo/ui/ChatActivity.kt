package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.View
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.im.sdk.view.ChatView
import com.im.sdk.view.vo.IMFileData
import com.im.sdk.view.vo.IMLocationVo
import com.im.sdk.view.vo.StrategyVo
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityChatBinding
import com.tmmtmm.demo.ui.dto.ButtonIdDto
import com.tmmtmm.demo.ui.view.CustomView

class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding

    private var aChatId = ""

    private var chatView: ChatView? = null

    companion object {

        private const val KEY_CHAT_ID = "key_chat_id"

        fun newInstance(context: Context, chatId: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_CHAT_ID, chatId)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initPrams() {
        aChatId = intent.getStringExtra(KEY_CHAT_ID) ?: ""
    }

    override fun initViews() {
//        val titleBarView = TitleBarView()
//        titleBarView.showTitleBar(
//            cRoot = binding.root,
//            leftBlock = {
//                finish()
//            },
//            leftRes = R.drawable.ic_demo_back,
//            rightRes = R.drawable.ic_demo_set_bg,
//            rightBlock = {
//
//            },
//            title = "聊天",
//        )
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivBgSet.setOnClickListener {


        }
        binding.btnSendMessage.setOnClickListener {
            SendMessageActivity.newInstance(this, aChatId)
        }


        KeyboardUtils.registerSoftInputChangedListener(this) { height ->
            if (height > 0) {
                chatView?.getChatListRecyclerView()?.scrollBy(0, height)
            }
        }

        binding.btnPin.setOnClickListener {
            showLoading()
            TmApplication.instance().viewModel?.getChatIsTop(aChatId) { isTop ->
                if (isTop) {
                    TmApplication.instance().viewModel?.setChatCloseTop(aChatId, {
                        hideLoading()
                        ToastUtils.showShort("取消置顶成功")
                    }, {
                        hideLoading()
                        ToastUtils.showShort("取消置顶失败")
                    })
                } else {
                    TmApplication.instance().viewModel?.setChatTop(aChatId, {
                        hideLoading()
                        ToastUtils.showShort("置顶成功")
                    }, {
                        hideLoading()
                        ToastUtils.showShort("置顶失败")
                    })
                }
            }
        }

    }

    override fun fetchData() {

        binding.chatList.removeAllViews()
        chatView = TmApplication.instance().imSdk?.createChatView(aChatId, this)
        binding.chatList.addView(chatView)
        chatView?.show()
        chatView?.setChatDelegate(object : ChatView.ChatDelegate {
            override fun onFileMessageClick(amid: String, data: IMFileData) {

            }

            override fun onImageMessageClick(amid: String, strategyVo: StrategyVo) {
                MediaPreviewActivity.newInstance(this@ChatActivity, strategyVo)

            }

            override fun onMiddleMessageClick(amid: String, tmpId: String, buttonId: String) {
                ToastUtils.showShort(buttonId)
            }

            override fun onButtonMessageClick(amid: String, buttonId: String) {
                try {
                    val dto = GsonUtils.fromJson<ButtonIdDto>(buttonId, ButtonIdDto::class.java)

                    if (dto.type == 19) {
                        ToastUtils.showShort("卡片消息")
                        //设置按钮不可点击，可设置多个按钮
                        TmApplication.instance().imSdk?.disableCardMessage(
                            amid,
                            mutableListOf(buttonId)
                        )
                    } else {
                        ToastUtils.showShort("通知消息")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onMessageMultipleChooseClick(messageMultipleChooser: ChatView.MessageMultipleChooser?) {

            }

            override fun onMomentMessageClick(amid: String, feedId: CharSequence?) {

            }

            override fun onLocationMessageClick(
                amid: String?,
                location: IMLocationVo
            ) {

            }

            override fun onAvatarLongPress(amid: String, aUid: String) {

            }

            override fun onAvatarClick(amid: String, aUid: String) {

            }

            override fun onMeetingRecordClick(amid: String, meetingType: Int) {

            }

            override fun onMessageReportAct(amid: String) {

            }

            override fun onMessageForwardAct(amid: String) {

            }

            override fun onMessageQuoteAct(
                amid: String,
                aUid: String,
                content: CharSequence?,
                position: Int
            ) {

            }

            override fun onRedPacketStatusChange(amid: String, id: String) {

            }

            override fun onRedPacketNoticeMessageClick(amid: String, outTradeNo: String) {

            }

            override fun onReferenceMessageClick(amid: String, messageBody: CharSequence) {

            }

            override fun onWebUrlLinkClick(amid: String, url: String?) {

            }

            override fun onTransferMessageClick(amid: String, orderId: String) {

            }

            override fun onPayMessageClick(
                amid: String,
                act: Int?,
                id: String?,
                associatedId: String?,
                outTradeNo: String?
            ) {

            }

            override fun onShowCustomMessageView(amid: String, content: String): View? {
                val customView = CustomView(this@ChatActivity)
                customView.bindData(content)
                return customView
            }

            override fun onCloseKeyBoard() {
                KeyboardUtils.hideSoftInput(this@ChatActivity)
            }


        })
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(this.window)
    }
}