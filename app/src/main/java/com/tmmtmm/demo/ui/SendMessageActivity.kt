package com.tmmtmm.demo.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.android.filepicker.config.FilePickerManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.tmmtmm.demo.api.SendCardAndTempMessage
import com.tmmtmm.demo.api.SendCardAndTempMessageRequest
import com.tmmtmm.demo.base.BaseActivity
import com.tmmtmm.demo.base.TmApplication
import com.tmmtmm.demo.databinding.ActivitySendMessageBinding
import com.tmmtmm.demo.manager.LoginManager
import com.tmmtmm.demo.utils.GlideEngine
import com.tmmtmm.demo.utils.Random
import com.tmmtmm.demo.utils.ResultContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class SendMessageActivity : BaseActivity() {

    private lateinit var binding: ActivitySendMessageBinding

    private lateinit var aChatId: String

    companion object {

        const val CHAT_ID = "chat_id"

        fun newInstance(context: Context, aChatId: String) {
            val intent = Intent(context, SendMessageActivity::class.java)
            intent.putExtra(CHAT_ID, aChatId)
            context.startActivity(intent)
        }
    }

    override fun contentView() {
        binding = ActivitySendMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun initPrams() {
        aChatId = intent.getStringExtra(CHAT_ID) ?: ""
    }

    override fun initViews() {
//        val titleBarView = TitleBarView()
//        titleBarView.showTitleBar(
//            lRoot = binding.root,
//            leftBlock = {
//                finish()
//            },
//            leftRes = R.drawable.ic_demo_back,
//            title = "发消息",
//        )
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnSendImageMessage.setOnClickListener {
            chooseImage()
        }

        binding.btnSendTextMessage.setOnClickListener {
            sendTextMessage()
        }

        binding.btnSendFileMessage.setOnClickListener {
            chooseFile()
        }

        binding.btnSendCardMessage.setOnClickListener {
            sendCardMessage()
        }
    }

    override fun fetchData() {

    }

    private fun sendTextMessage() {
        val content = binding.etMessageContent.text.toString()
        if (content.isBlank()) {
            return
        }
        showLoading()
        lifecycleScope.launch(Dispatchers.IO) {
            TmApplication.instance().imSdk?.sendTextMessage(aChatId, Random.create(6), content)
            withContext(Dispatchers.Main) {
                hideLoading()
                binding.etMessageContent.setText("")
                KeyboardUtils.hideSoftInput(binding.etMessageContent)
                finish()
            }
        }
    }

    private fun chooseImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .isGif(true)
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    val mediaList = result ?: mutableListOf<LocalMedia>()
                    for (localMedia in mediaList) {
                        localMedia ?: continue
                        if (PictureMimeType.isHasVideo(localMedia.mimeType)) {
//                            if (it?.size > 500 * 1048576) {
//                                ToastUtil.toastShort(
//                                    String.format(
//                                        getString(R.string.string_max_video_size),
//                                        500
//                                    )
//                                )
//                                return
//                            }
//                            mChatMessageViewModel
//                                .sendVideoMessage(
//                                    typeTm = TmAttachmentType.VIDEO,
//                                    path = it.realPath,
//                                    duration = it.duration,
//                                    chatId,
//                                    TmLoginManager.getUserId(),
//                                    liveData = liveData
//                                )
                        } else {
                            if (PictureMimeType.isHasGif(localMedia.mimeType) && localMedia.size > 10 * 1048576) {
//                                ToastUtil.toastShort(
//                                    String.format(
//                                        getString(R.string.string_max_gif_size),
//                                        10
//                                    )
//                                )
                                return
                            } else if (localMedia.size > 100 * 1048576) {
//                                ToastUtil.toastShort(
//                                    String.format(
//                                        getString(R.string.string_max_photo_size),
//                                        100
//                                    )
//                                )
                                return
                            }

                            val isOriginal =
                                if (PictureMimeType.isHasGif(localMedia.mimeType)) {
                                    1
                                } else {
                                    if (localMedia.isOriginal) 1 else 0
                                }

                            val width = if (localMedia.width == 0) {
                                ImageUtils.getBitmap(localMedia.realPath).width
                            } else {
                                localMedia.width
                            }

                            val height = if (localMedia.height == 0) {
                                ImageUtils.getBitmap(localMedia.realPath).height
                            } else {
                                localMedia.height
                            }

                            TmApplication.instance().imSdk?.sendImageMessage(
                                aChatId = aChatId,
                                amid = Random.create(6),
                                path = localMedia.realPath,
                                isOrigin = true,
                            )
                            finish()
                        }
                    }
                }

                override fun onCancel() {

                }
            })
    }


    private fun chooseFile() {
        startActivityLauncher.launch(true)
//        FilePickerManager
//            .from(this)
//            .enableSingleChoice()
//            .forResult(FilePickerManager.REQUEST_CODE)
    }

    private fun sendFileMessage() {

        val list = FilePickerManager.obtainData()
        if (list.isNotEmpty()) {
            val file = File(list[0])
            val fileType = FileUtils.getFileExtension(file)
            //val format = FormatUtils.getFileType(file.name)

            TmApplication.instance().imSdk?.sendAttachmentMessage(
                aChatId = aChatId,
                amid = Random.create(6),
                path = file.absolutePath,
            )
        }
    }


    private fun sendCardMessage() {
        showLoading()
        lifecycleScope.launch(Dispatchers.IO) {
            val result = SendCardAndTempMessage.execute(
                SendCardAndTempMessageRequest(
                    aChatId = aChatId,
                    LoginManager.INSTANCE.getUserId(),
                    sendTime = System.currentTimeMillis()
                )
            )
            withContext(Dispatchers.Main) {
                hideLoading()
                finish()
            }
        }
    }

    private val startActivityLauncher =
        registerForActivityResult(ResultContract()) { activityResult ->
            if (activityResult == null) {
                return@registerForActivityResult
            }

            val uri = activityResult.data
            if (uri == null) {
                return@registerForActivityResult
            }
            val path = uri.toString()
//            val path = RealPathUtil.getRealPath(this,uri)
//            val fileType = FileUtils.getFileExtension(file)
            //val format = FormatUtils.getFileType(file.name)
            if (path.isNullOrBlank()) {
                return@registerForActivityResult
            }
            TmApplication.instance().imSdk?.sendAttachmentMessage(
                aChatId = aChatId,
                amid = Random.create(6),
                path = path,
            )
        }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            if (requestCode == FilePickerManager.REQUEST_CODE) {
//                sendFileMessage()
//                finish()
//            }
//        }
//    }
}