package com.tmmtmm.demo.ui

//import com.chad.library.adapter.base.BaseBinderAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import com.allen.library.shape.ShapeTextView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ThreadUtils
import com.im.sdk.IMSdk
import com.im.sdk.constant.enums.LanguageType
import com.im.sdk.dto.IMAvatar
import com.im.sdk.view.ConversationView
import com.im.sdk.view.IMConversationViewModel
import com.im.sdk.view.selector.SelectAll
import com.im.sdk.view.selector.SelectPart
import com.im.sdk.view.selector.SelectorFactory
import com.im.sdk.view.selector.UnReadSelectPart
import com.im.sdk.view.selector.UnSelectPart
import com.im.sdk.view.vo.IMConversationMarker
import com.im.sdk.view.vo.IMConversationSubTitle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
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

    private var hideConversationIds = mutableListOf<String>("1471471477_1471471478")

    private var conversationViewModel: IMConversationViewModel? = null

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
        checkPermission()

    }

    private fun checkPermission() {
        if (com.lzf.easyfloat.permission.PermissionUtils.checkPermission(this)) {
            showAppFloat()
        } else {
            android.app.AlertDialog.Builder(this)
                .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                .setPositiveButton("去开启") { _, _ ->
                    showAppFloat()
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }

    private fun showAppFloat() {
        val gravity = Gravity.END.or(Gravity.BOTTOM)
        EasyFloat.with(this.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(gravity, -20, -100)
            .setLayout(R.layout.layout_btn_login) {
                val layout_btn_login = it?.findViewById<ShapeTextView>(R.id.tvLoginAction)
                layout_btn_login?.setOnClickListener {
                    loginout()
                }
                val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
                    "退出登录"
                } else {
                    "登录"
                }
                layout_btn_login?.text = loginText
//                // 解决 ListView 拖拽滑动冲突
//                it.findViewById<ListView>(R.id.lv_test).apply {
//                    adapter = MyAdapter(
//                        this@MainActivity,
//                        arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "...")
//                    )
//
//                    // 监听 ListView 的触摸事件，手指触摸时关闭拖拽，手指离开重新开启拖拽
//                    setOnTouchListener { _, event ->
//                        logger.e("listView: ${event.action}")
//                        EasyFloat.appFloatDragEnable(event?.action == MotionEvent.ACTION_UP)
//                        false
//                    }
//                }
            }
            .registerCallback {
                drag { _, motionEvent ->
//                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
//                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
//                            setVibrator(inRange)
//                            view.findViewById<TextView>(com.lzf.easyfloat.R.id.tv_delete).text =
//                                if (inRange) "松手删除" else "删除浮窗"
//
//                            view.findViewById<ImageView>(com.lzf.easyfloat.R.id.iv_delete)
//                                .setImageResource(
//                                    if (inRange) com.lzf.easyfloat.R.drawable.icon_delete_selected
//                                    else com.lzf.easyfloat.R.drawable.icon_delete_normal
//                                )
//                        }
//
//                        override fun touchUpInRange() {
//                            EasyFloat.dismiss()
//                        }
//                    }, showPattern = ShowPattern.ALL_TIME)
                }
            }
            .show()
        val btnLogin = EasyFloat.getFloatView()?.findViewById<ShapeTextView>(R.id.tvLoginAction)
        val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
            "退出登录"
        } else {
            "登录"
        }
        btnLogin?.text = loginText
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
            "退出登录"
        } else {
            "登录"
        }

        mBinding.tvLeft.text = loginText
        checkPermission()
    }

    override fun onResume() {
        super.onResume()
        val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
            "退出登录"
        } else {
            "登录"
        }

        mBinding.tvLeft.text = loginText
        mBinding.tvTitle.text = "聊天" + "(${LoginManager.INSTANCE.getUserPhone()})"
        checkPermission()
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
//     Lo    }
//        )

        val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
            "退出登录"
        } else {
            "登录"
        }

        mBinding.tvLeft.text = loginText

        mBinding.tvLeft.setOnClickListener {
            loginout()
//            checkPermission()
        }

        mBinding.tvRight.setOnClickListener {
            createGroup()
        }

        mBinding.tvTitle.text = "聊天" + "(${LoginManager.INSTANCE.getUserPhone()})"

        TmApplication.instance().imSdk?.setCurrentLanguage(LanguageType.SimplifiedChinese)


        val folder = LoginManager.INSTANCE.getFolder()
        val selector = if (folder.isBlank()) {
            SelectorFactory.allOf()
        } else {
            SelectorFactory.unPartOf(hideConversationIds)
        }

        conversationViewModel = TmApplication.instance().imSdk?.createConversationViewModel(
            selector
        )

        val conversationView = conversationViewModel?.getView(this)
        mBinding.fragment.addView(conversationView)



        conversationView?.setConversationDelegate(object :
            ConversationView.ConversationDelegate {

            override fun onItemClick(aChatId: String) {
                TmApplication.instance().viewModel = conversationViewModel
                if (aChatId == FOLDER_ID) {
                    ConversationActivity.newInstance(this@MainActivity, aChatId)
                    return
                }
                ChatActivity.newInstance(this@MainActivity, aChatId)
            }

            override fun onShowConversationMarker(aChatIds: List<String>) {
                if (!aChatIds.contains(FOLDER_ID)) return
                val markerList = mutableListOf<IMConversationMarker>()
                val marker = IMConversationMarker(
                    aChatId = FOLDER_ID, markerVo = IMAvatar(R.drawable.ic_marker)
                )
                markerList.add(marker)

                conversationViewModel?.setConversationMarker(markerList)
            }

            override fun onShowConversationSubTitle(aChatIds: List<String>) {
                if (!aChatIds.contains(FOLDER_ID)) return
                val subNameList = mutableListOf<IMConversationSubTitle>()
                val subNameDto = IMConversationSubTitle(aChatId = FOLDER_ID, subTitle = "屏蔽的")
                subNameList.add(subNameDto)

                conversationViewModel?.setConversationSubTitle(subNameList)
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

        mBinding.btnMoreAction.setOnClickListener {
            XPopup.Builder(this)
                .hasStatusBarShadow(false) //.dismissOnBackPressed(false)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
                .isDarkTheme(true) //                        .isViewMode(true)
                //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                .atView(mBinding.btnMoreAction)
                .asAttachList(
                    arrayOf<String>("重置","筛选未读回话"), intArrayOf()
                ) { position, text ->
                    when (position) {
                        0 -> {
                            showOriginConversations()
                        }

                        1 -> {
                            showUnReadConversations()
                        }

                        else -> {}
                    }

                }
                .show()
        }
    }

    override fun fetchData() {

    }

    private fun addFolder(conversationViewModel: IMConversationViewModel?) {

        val drawable = ResourcesCompat.getDrawable(resources, applicationInfo.icon, theme)
        val avatar = ImageUtils.drawable2Bytes(drawable)
        val image = IMAvatar(avatar)
        conversationViewModel?.setFolder(
            aChatId = FOLDER_ID,
            content = "共${hideConversationIds.size}条会话",
            name = FOLDER_NAME,
            folderIcon = image
        )

        LoginManager.INSTANCE.setFolder(FOLDER_ID)

//        conversationViewModel?.updateSelector(unSelectAChatIds = hideConversationIds)

        hideConversationIds = mutableListOf("1471471478_1471471479")
        val hideSelector = UnSelectPart(hideConversationIds)
        val folderSelector = SelectPart(mutableListOf(FOLDER_ID))
        val selector =
            conversationViewModel?.getCurrentSelector()?.and(hideSelector)?.or(folderSelector)
        selector?.let { conversationViewModel.replace(it) }
    }

    private fun removeFolder(conversationViewModel: IMConversationViewModel?) {
        conversationViewModel?.removeFolder(FOLDER_ID)
//        conversationViewModel?.updateSelector()
        val hideSelector = SelectPart(hideConversationIds)
        val selector = conversationViewModel?.getCurrentSelector()?.or(hideSelector)
        if (selector != null) {
            conversationViewModel.replace(selector)
        }

        LoginManager.INSTANCE.setFolder("")
    }

    private fun showOriginConversations() {
        val selector = SelectAll()
        selector.let { conversationViewModel?.replace(it) }
    }

    private fun showUnReadConversations() {
        val unReadSelector = UnReadSelectPart()
        val selector = conversationViewModel?.getCurrentSelector()?.and(unReadSelector)
        selector?.let { conversationViewModel?.replace(it) }

    }


    private fun addFolderMarkerAndSubTitle(conversationViewModel: IMConversationViewModel?) {
        val markerList = mutableListOf<IMConversationMarker>()
        val marker = IMConversationMarker(
            aChatId = FOLDER_ID, markerVo = IMAvatar(R.drawable.ic_marker)
        )
        markerList.add(marker)

        conversationViewModel?.setConversationMarker(markerList)


        val subNameList = mutableListOf<IMConversationSubTitle>()
        val subNameDto = IMConversationSubTitle(aChatId = FOLDER_ID, subTitle = "屏蔽的")
        subNameList.add(subNameDto)

        conversationViewModel?.setConversationSubTitle(subNameList)
    }



    private fun loginout() {
        showLoading()
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

        if (LoginManager.INSTANCE.getUserId().isBlank()) {
            hideLoading()
            LoginActivity.newInstance(this)
        } else {
            LoginManager.INSTANCE.setUserId("")
            LoginManager.INSTANCE.setUserPhone("")
            TmApplication.instance().imSdk?.loginOut()
            ThreadUtils.runOnUiThreadDelayed({
                hideLoading()
//            LoginActivity.newInstance(this)
//                finish()
            }, 1000)
            mBinding.tvLeft.text = "登陆"
        }

        val btnLogin = EasyFloat.getFloatView()?.findViewById<ShapeTextView>(R.id.tvLoginAction)
        val loginText = if (LoginManager.INSTANCE.getUserId().isNotBlank()) {
            "退出登录"
        } else {
            "登录"
        }
        btnLogin?.text = loginText

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
                            hideConversationIds = mutableListOf(aChatID)
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