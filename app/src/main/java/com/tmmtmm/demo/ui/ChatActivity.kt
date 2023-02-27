package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.im.sdk.view.ChatView
import com.im.sdk.view.vo.StrategyVo
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityChatBinding

class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding

    private var aChatId = ""

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
//                binding.chatList.forceScrollToPosition()
            }
        }
    }

    override fun fetchData() {

        binding.chatList.removeAllViews()
        val chatView = TmApplication.instance().imSdk?.createChatView(aChatId, this)
        binding.chatList.addView(chatView)
        chatView?.show()
        chatView?.setChatDelegate(object : ChatView.ChatDelegate {
            //CardMessage按钮点击事件
//            override fun onCardMessageClick(amid: String, buttonId: String) {
//                ToastUtils.showShort(buttonId)
//                //设置按钮不可点击，可设置多个按钮
//                TmApplication.instance().imSdk?.disableCardMessage(amid, mutableListOf(buttonId))
//            }
            override fun onButtonMessageClick(amid: String, buttonId: String) {
                ToastUtils.showShort("通知消息")

            }

            override fun onCloseKeyBoardPanel() {
                
            }

            override fun onCoinOrderDetails(id: String) {
                
            }

            override fun onForwardMessageSeeSee(messageBody: CharSequence) {
                
            }

            override fun onMapLocation(
                amid: String?,
                lat: Double,
                lon: Double,
                zoom: Float?,
                addressName: String?,
                address: String?
            ) {
                
            }

            override fun onMediaPreview(strategyVo: StrategyVo) {
                
            }

            override fun onMeetingCall(meetingType: Int) {
                
            }

            override fun onMessageDownFile(amid: String) {
                
            }

            override fun onMessageForward(amid: String) {
                
            }

            override fun onMessageMultipleChooseClick(messageMultipleChooser: ChatView.MessageMultipleChooser?) {
                
            }

            override fun onMessageQuote(
                amid: String,
                aUid: String,
                content: CharSequence?,
                position: Int
            ) {
                
            }

            override fun onMessageReport(amid: String) {
                
            }

            override fun onMiddleMessageClick(
                amid: String,
                tmpId: String,
                buttonId: String
            ) {
                ToastUtils.showShort(buttonId)
            }

            override fun onMomentDetail(feedId: CharSequence?) {
                
            }

            override fun onOpenPdf(filePath: String, fileName: String, amid: String) {
                
            }

            override fun onRedPacketDetail(aChatId: String, outTradeNo: String) {
                
            }

            override fun onRedPacketStatusChange(amid: String, aChatId: String, id: String) {
                
            }

            override fun onTmmPayClick(
                act: Int?,
                id: String?,
                associatedId: String?,
                outTradeNo: String?
            ) {
                
            }

            override fun onUserAvatarClick(amid: String, aChatId: String, aUid: String) {
                
            }

            override fun onUserAvatarLongClick(amid: String, aChatId: String, aUid: String) {
                
            }

            override fun onWebUrlLinkClick(url: String?) {
                
            }

//            override fun onShowCustomMessageView(amid: String, content: String): View {
//                val customView = CustomView(this@ChatActivity)
//                customView.bindData(content)
//                return customView
//            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(this.window)
    }
}