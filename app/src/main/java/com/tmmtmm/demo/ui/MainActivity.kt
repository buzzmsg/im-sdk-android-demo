package com.tmmtmm.demo.ui

//import com.chad.library.adapter.base.BaseBinderAdapter
import android.content.Context
import android.content.Intent
import com.lxj.xpopup.XPopup
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.databinding.ActivityMainBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.view.TitleBarView
import com.tmmtmm.sdk.ui.view.TmConversationLayout
import java.security.AccessController.getContext

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
//    private val mAdapter = BaseBinderAdapter()

    companion object {
        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        mBinding = ActivityMainBinding.inflate(layoutInflater).bindView(this)
    }

    override fun initPrams() {

    }

    override fun initViews() {
        val titleBarView = TitleBarView()
        titleBarView.showTitleBar(
            cRoot = mBinding.root,
            title = "聊天",
            rightText = "创建聊天",
            rightBlock = {
                enterChat()
//                createGroup()
            }
        )

        mBinding.conversationLayout.setItemClickCallBack(object :
            TmConversationLayout.ItemClickCallBack {
            override fun onItemClick(chatId: String?) {

            }
        })
    }

    override fun fetchData() {

    }

    fun enterChat() {
        ChatActivity.newInstance(this, "")
    }

    fun createGroup() {
        XPopup.Builder(this)
            .hasStatusBarShadow(false) //.dismissOnBackPressed(false)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
            .autoOpenSoftInput(true)
            .isDarkTheme(true) //                        .isViewMode(true)
            //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
            .asInputConfirm(
                "创建群聊", "", null, "用户id"
            ) {
                //                                          new XPopup.Builder(getContext()).asLoading().show();
            }
            .show()
    }

}