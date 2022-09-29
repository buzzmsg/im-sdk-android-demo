package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.databinding.ActivityChatBinding
import com.tmmtmm.demo.ui.view.TitleBarView
import com.tmmtmm.sdk.TMM

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
        val titleBarView = TitleBarView()
        titleBarView.showTitleBar(
            cRoot = binding.root,
            leftBlock = {
                finish()
            },
            leftRes = R.drawable.ic_back_black,
            title = "聊天",
        )
        binding.btnSendMessage.setOnClickListener {
            val content = binding.etMessageContent.text.toString()
            if (content.isBlank()) {
                return@setOnClickListener
            }
            TMM.INSTANCE.sendTextMessage(content, aChatId)
        }
    }

    override fun fetchData() {
    }
}