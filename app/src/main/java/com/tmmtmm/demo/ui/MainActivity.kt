package com.tmmtmm.demo.ui

//import com.chad.library.adapter.base.BaseBinderAdapter
import android.content.Context
import android.content.Intent
import android.text.Html
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityMainBinding
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.view.TitleBarView
import com.tmmtmm.demo.utils.MD5
import com.im.sdk.IMSdk
import com.im.sdk.ui.view.ConversationView
import com.tmmtmm.demo.manager.LoginManager
import org.xml.sax.InputSource
import org.xml.sax.Parser
import org.xml.sax.XMLReader
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.helpers.ParserFactory
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParserFactory

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
            leftText = "加入测试群",
            leftBlock = {
                joinTestGroup()
            },
            rightText = "创建聊天",
            rightBlock = {
                createGroup()
            }
        )

        mBinding.conversationLayout.setConversationDelegate(object :
            ConversationView.ConversationDelegate {
            override fun onItemClick(aChatId: String) {
                ChatActivity.newInstance(this@MainActivity, aChatId)
            }
        })
    }

    override fun fetchData() {

    }

    private fun joinTestGroup() {
        showLoading()
        val auid = LoginManager.INSTANCE.getUserId()
        TmApplication.instance().imSdk?.joinChat(auid, { aChatId ->
            ThreadUtils.runOnUiThread {
                hideLoading()
                ChatActivity.newInstance(this@MainActivity, aChatId)
            }
        }, { msg ->
            ThreadUtils.runOnUiThread {
                hideLoading()
                ToastUtils.showLong(msg)
            }
        })
    }

    private fun createGroup() {
        XPopup.Builder(this)
            .hasStatusBarShadow(false) //.dismissOnBackPressed(false)
            .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
            .autoOpenSoftInput(true)
            .isDarkTheme(true) //                        .isViewMode(true)
            //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
            .asInputConfirm(
                "创建聊天", "", null, "用户手机号"
            ) { phone ->
                //                                          new XPopup.Builder(getContext()).asLoading().show();
                val auid = MD5.create(phone)
                val minePhone = LoginManager.INSTANCE.getUserPhone()
                val aChatID = if (phone < minePhone) {
                    "${phone}_${minePhone}"
                } else {
                    "${minePhone}_${phone}"
                }
                TmApplication.instance().imSdk?.createChat(
                    aChatId = aChatID,
                    chatName = aChatID,
                    auids = mutableListOf(auid),
                    object : IMSdk.CreateChatDelegate {
                        override fun onSucc() {
                            ChatActivity.newInstance(this@MainActivity, aChatID)
                        }

                        override fun onError(code: Int?, errorMsg: String?) {

                        }
                    })
            }
            .show()
    }


    class myHandler() : DefaultHandler() {
        var buf: StringBuffer? = null
        var str: String = ""

        override fun startDocument() {
//            super.startDocument()
            buf = StringBuffer()
        }

        override fun endDocument() {
//            super.endDocument()

        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
//            super.endElement(uri, localName, qName)
            if (buf == null) return
            str = buf.toString()
            buf?.delete(0, buf!!.length)
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
//            super.characters(ch, start, length)
            buf?.append(ch, start, length)
        }
    }

}