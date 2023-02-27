package com.tmmtmm.demo.ui

//import com.chad.library.adapter.base.BaseBinderAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.im.sdk.IMSdk
import com.im.sdk.constant.enums.LanguageType
import com.im.sdk.dto.ImImageResourcesInfo
import com.im.sdk.view.ConversationView
import com.im.sdk.view.IMConversationViewModel
import com.im.sdk.view.selector.SelectorFactory
import com.im.sdk.view.vo.IMConversationMarker
import com.im.sdk.view.vo.IMConversationSubTitle
import com.lxj.xpopup.XPopup
import com.tmmtmm.demo.R
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivityMainBinding
import com.tmmtmm.demo.manager.LoginManager
import com.tmmtmm.demo.ui.ext.bindView
import com.tmmtmm.demo.ui.ext.click
import com.tmmtmm.demo.utils.MD5
import org.xml.sax.helpers.DefaultHandler

class MainActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMainBinding
//    private val mAdapter = BaseBinderAdapter()

    private var hideConversationIds = mutableListOf("TestAli")

    companion object {

        const val FOLDER_NAME = "不感兴趣的聊天"

        const val FOLDER_ID = "test"


        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        mBinding = ActivityMainBinding.inflate(layoutInflater).bindView(this)
        PermissionUtils.permission(PermissionConstants.STORAGE).request()
    }

    override fun initPrams() {
        val withAppendedPath = Uri.parse("wss://dev-sdk-tcp.imtmm.com:7502").buildUpon()
        Log.e("11111111111111", "initPrams() called${withAppendedPath}")
    }

    override fun initViews() {
//        val titleBarView = TitleBarView()
//        titleBarView.showTitleBar(
//            cRoot = mBinding.root,
//            title = "聊天",
//            leftText = "加入测试群",
//            leftBlock = {
//                joinTestGroup()
//            },
//            rightText = "创建聊天",
//            rightBlock = {
//                createGroup()
//            }
//        )
        mBinding.tvLeft.setOnClickListener {
            joinTestGroup()
        }

        mBinding.tvRight.setOnClickListener {
            createGroup()
        }

        TmApplication.instance().imSdk?.setCurrentLanguage(LanguageType.SimplifiedChinese)


        val folder = LoginManager.INSTANCE.getFolder()
        val selector = if (folder.isBlank()) {
            SelectorFactory.allOf()
        } else {
            SelectorFactory.unPartOf(hideConversationIds)
        }

        val conversationViewModel = TmApplication.instance().imSdk?.createConversationViewModel(
            selector
        )

        val conversationView = conversationViewModel?.getView(this)
        mBinding.fragment.addView(conversationView)



        conversationView?.setConversationDelegate(object :
            ConversationView.ConversationDelegate {
            override fun onCompleteAddConversation(aChatIds: MutableList<String>) {

            }

            override fun onItemClick(aChatId: String) {
                if (aChatId == FOLDER_ID) {
                    ConversationActivity.newInstance(this@MainActivity, aChatId)
                    return
                }
                ChatActivity.newInstance(this@MainActivity, aChatId)
            }
        })

        mBinding.btnAddFolder.click {
            addFolder(conversationViewModel)
        }

        mBinding.btnRemoveFolder.click {
            removeFolder(conversationViewModel)
        }

        mBinding.btnAddFolderMarker.click {
            addFolderMarkerAndSubTitle(conversationViewModel)
        }
    }

    override fun fetchData() {

    }

    private fun addFolder(conversationViewModel: IMConversationViewModel?) {

        val drawable = ResourcesCompat.getDrawable(resources, applicationInfo.icon, theme)
        val avatar = ImageUtils.drawable2Bytes(drawable)
        val image = ImImageResourcesInfo(avatar)
        conversationViewModel?.setFolder(
            aChatId = FOLDER_ID,
            content = "共${hideConversationIds.size}条会话",
            name = FOLDER_NAME,
            resourcesInfo = image
        )

        LoginManager.INSTANCE.setFolder(FOLDER_ID)

        conversationViewModel?.updateSelector(unSelectAChatIds = hideConversationIds)

    }


    private fun addFolderMarkerAndSubTitle(conversationViewModel: IMConversationViewModel?) {
        val markerList = mutableListOf<IMConversationMarker>()
        val marker = IMConversationMarker(
            aChatId = FOLDER_ID, markerVo = ImImageResourcesInfo(R.drawable.ic_marker)
        )
        markerList.add(marker)

        conversationViewModel?.updateConversationMarker(markerList)


        val subNameList = mutableListOf<IMConversationSubTitle>()
        val subNameDto = IMConversationSubTitle(aChatId = FOLDER_ID, subTitle = "屏蔽的")
        subNameList.add(subNameDto)

        conversationViewModel?.updateConversationSubName(subNameList)
    }


    private fun removeFolder(conversationViewModel: IMConversationViewModel?) {
        conversationViewModel?.removeFolder(FOLDER_ID)
        conversationViewModel?.updateSelector()
        LoginManager.INSTANCE.setFolder("")
    }

    private fun joinTestGroup() {
//        showLoading()
//        val auid = LoginManager.INSTANCE.getUserId()
//        TmApplication.instance().imSdk?.joinChat(auid, { aChatId ->
//            ThreadUtils.runOnUiThread {
//                hideLoading()
//                ChatActivity.newInstance(this@MainActivity, aChatId)
//            }
//        }, { msg ->
//            ThreadUtils.runOnUiThread {
//                hideLoading()
//                ToastUtils.showLong(msg)
//            }
//        })
//        TmApplication.instance().imSdk?.loginOut()
//        ThreadUtils.runOnUiThreadDelayed({
//            hideLoading()
//            LoginActivity.newInstance(this)
//            finish()
//        }, 1000)

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