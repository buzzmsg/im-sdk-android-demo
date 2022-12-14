package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.im.sdk.ui.view.ChatView
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityChatBinding
import com.tmmtmm.demo.ui.view.CustomView

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
        binding.chatList.createChat(aChatId)
        binding.chatList.setChatDelegate(object : ChatView.ChatDelegate {
            //CardMessage按钮点击事件
            override fun onCardMessageClick(amid: String, buttonId: String) {
                ToastUtils.showShort(buttonId)
                //设置按钮不可点击，可设置多个按钮
                TmApplication.instance().imSdk?.disableCardMessage(amid, mutableListOf(buttonId))
            }

            override fun onMiddleMessageClick(
                amid: String,
                tmpId: String,
                buttonId: String
            ) {
                ToastUtils.showShort(buttonId)
            }

            override fun onNotificationMessageClick(amid: String, buttonId: String) {
                ToastUtils.showShort("通知消息")
            }

            override fun onShowCustomMessageView(amid: String, content: String): View {
                val customView = CustomView(this@ChatActivity)
                customView.bindData(content)
                return customView
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(this.window)
    }
}