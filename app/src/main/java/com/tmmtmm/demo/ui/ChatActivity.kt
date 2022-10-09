package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.KeyboardUtils
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityChatBinding
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.demo.ui.view.TitleBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            leftRes = R.drawable.ic_demo_back,
            rightRes = R.drawable.ic_demo_set_bg,
            rightBlock = {

            },
            title = "聊天",
        )
        binding.btnSendMessage.click {
            val content = binding.etMessageContent.text.toString()
            if (content.isBlank()) {
                return@click
            }
            showLoading()
            lifecycleScope.launch(Dispatchers.IO){
                TmApplication.instance().imSdk?.sendTextMessage(content, aChatId, kotlin.random.Random(6).toString())
                withContext(Dispatchers.Main){
                    hideLoading()
                    binding.etMessageContent.setText("")
                    KeyboardUtils.hideSoftInput(binding.etMessageContent)
                }
            }

        }

        KeyboardUtils.registerSoftInputChangedListener(this) { height ->
            if (height > 0){
                binding.conversationLayout.forceScrollToPosition()
            }
        }
    }

    override fun fetchData() {
        binding.conversationLayout.createChat(aChatId)
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(this.window)
    }
}