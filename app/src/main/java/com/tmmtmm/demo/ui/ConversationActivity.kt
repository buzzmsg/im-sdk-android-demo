package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import com.im.sdk.view.ConversationView
import com.im.sdk.view.selector.SelectorFactory
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityConversationBinding
import com.tmmtmm.demo.ui.ext.bindView

class ConversationActivity : BaseActivity() {

    private lateinit var aChatId: String

    private lateinit var binding: ActivityConversationBinding

    private var hideConversationIds = mutableListOf("1471471477_1471471478")

    companion object {
        fun newInstance(context: Context, chatId: String) {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("id", chatId)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        binding = ActivityConversationBinding.inflate(layoutInflater).bindView(this)
    }

    override fun initPrams() {
        aChatId = intent.getStringExtra("id") ?: ""
    }

    override fun initViews() {
        binding.tvLeft.setOnClickListener {
            finish()
        }

        val conversationViewModel = TmApplication.instance().imSdk?.createConversationViewModel(
            SelectorFactory.partOf(
            hideConversationIds
        ))

        val conversationView = conversationViewModel?.getView(this)
        binding.fragment.addView(conversationView)

        conversationView?.setConversationDelegate(object :
            ConversationView.ConversationDelegate {

            override fun onItemClick(aChatId: String) {
                TmApplication.instance().viewModel = conversationViewModel
                ChatActivity.newInstance(this@ConversationActivity, aChatId)
            }

            override fun onShowConversationMarker(aChatIds: List<String>) {

            }

            override fun onShowConversationSubTitle(aChatIds: List<String>) {

            }
        })
    }

    override fun fetchData() {
    }
}