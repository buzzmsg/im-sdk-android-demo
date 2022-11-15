package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.im.sdk.ui.selector.SelectorFactory
import com.im.sdk.ui.view.ConversationView
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityConversationBinding
import com.tmmtmm.demo.databinding.ActivityMainBinding
import com.tmmtmm.demo.ui.ext.bindView

class ConversationActivity : BaseActivity() {

    private lateinit var aChatId: String

    private lateinit var binding: ActivityConversationBinding

    private var hideConversationIds = mutableListOf("TestAli")

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
                ChatActivity.newInstance(this@ConversationActivity, aChatId)
            }
        })
    }

    override fun fetchData() {
    }
}