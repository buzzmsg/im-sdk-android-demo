package com.im.sdk.ui.view.recyclerview.decoration

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.im.sdk.R
import com.im.sdk.time.ChatTime
import com.im.sdk.ui.ext.dpToPx
import com.im.sdk.ui.ext.getColor
import com.im.sdk.ui.view.recyclerview.listener.ChatAvatarDecorationListener
import com.im.sdk.ui.view.vo.TmmMessageVo


object RvDecoration {
    private const val TAG = "RvDecoration"
//    fun buildNormalDecoration(context: Context, onQuick: ((Int) -> Any?)): NormalDecoration {
//        val headerContentColor = ContextCompat.getColor(context, R.color.color_F3F5F9)
//        val textColor = ContextCompat.getColor(context, R.color.text_A2A8C3)
//        val color = ContextCompat.getColor(context, R.color.transparent)
//        val resources = context.resources
//        val normalDecoration = object : NormalDecoration(
//            resources.getDimensionPixelSize(R.dimen.dp_30),
//            resources.getDimensionPixelSize(R.dimen.dp_16),
//            resources.getDimensionPixelSize(R.dimen.sp_16),
//            textColor,
//            headerContentColor,
//            color = color,
//            size = resources.getDimensionPixelSize(R.dimen.dp_0_5)
//        ) {
//            override fun getHeaderName(pos: Int): String {
//                try {
//                    val model = onQuick.invoke(pos) ?: return ""
//                    if (model is QuickInterface)
//                        return model.getQuickStr()
//                    return ""
//                } catch (e: Exception) {
//                    return ""
//                }
//            }
//        }
//        normalDecoration.setHeaderContentColor(headerContentColor)
//        return normalDecoration
//    }
//
//    fun buildChatDecoration(context: Context, onQuick: ((Int) -> Any?)): NormalDecoration {
//        val headerContentColor = ContextCompat.getColor(context, R.color.color_F3F5F9)
//        val textColor = ContextCompat.getColor(context, R.color.text_A2A8C3)
//        val color = ContextCompat.getColor(context, R.color.transparent)
//        val resources = context.resources
//        val normalDecoration = object : NormalDecoration(
//            resources.getDimensionPixelSize(R.dimen.dp_30),
//            0,
//            resources.getDimensionPixelSize(R.dimen.sp_14),
//            textColor,
//            headerContentColor,
//            color = color,
//            size = resources.getDimensionPixelSize(R.dimen.dp_10),
//        ) {
//            override fun getHeaderName(pos: Int): String? {
//                try {
//                    val model = onQuick.invoke(pos) ?: return ""
//                    if (model is TmmMessageVo) {
////                        return TimeDisplayUtil.getFriendlyTimeSpanByNow(model.createTime)
////                        return TimeDisplayUtil.getFriendlyTimeSpanByNow(model.displayTime)
//                        val time = ChatTime.getChatSectionSpanString(
//                            model.displayTime
//                        )
//                        return time
//                    }
//                    return ""
//                } catch (e: Exception) {
//                    return ""
//                }
//            }
//        }
//        normalDecoration.setOnDecorationHeadDraw(object : NormalDecoration.OnDecorationHeadDraw {
//            override fun getHeaderView(pos: Int, parent: ViewGroup?): View {
//                val sectionView = LayoutInflater.from(parent?.context)
//                    .inflate(R.layout.chat_pay_message_section_time_view, parent, false)
//                val tvSectionText =
//                    sectionView.findViewById<AppCompatTextView>(R.id.tvMessageSectionTime)
//                try {
//                    val model = onQuick.invoke(pos)
//                    if (model is TmmMessageVo) {
////                        val time = TimeDisplayUtil.getFriendlyTimeSpanByNow(model.createTime)
////                        val time = TimeDisplayUtil.getFriendlyTimeSpanByNow(model.displayTime)
//                        val time = ChatTime.getChatSectionSpanString(
//                            model.displayTime
//                        )
//                        tvSectionText.text = time
//                    }
//                } catch (e: Exception) {
//
//                }
//                return sectionView
//            }
//        })
//        normalDecoration.setHeaderContentColor(headerContentColor)
//        return normalDecoration
//    }

    fun buildNewChatDecoration(
        context: Context,
        onQuick: ((Int) -> Any?),
        isShowHistoryDecoration: ((Int) -> Boolean?)? = null,
    ): ChatStickyDecoration {
        val listener = object : ChatAvatarDecorationListener {
            override fun getGroupName(position: Int): String? {
                try {
                    val model = onQuick.invoke(position) ?: return ""
                    if (model is TmmMessageVo) {
                        return ChatTime.getChatSectionSpanString(
                            model.displayTime
                        )
                    }
                } catch (e: Exception) {

                }
                return ""

            }

            override fun getGroupView(position: Int): View? {

                val sectionView = LayoutInflater.from(context)
                    .inflate(R.layout.chat_pay_message_section_time_view, null, false)
                val tvSectionText =
                    sectionView.findViewById<AppCompatTextView>(R.id.tvMessageSectionTime)
                try {
                    val model = onQuick.invoke(position) ?: return sectionView
                    if (model is TmmMessageVo) {

                        val time = ChatTime.getChatSectionSpanString(
                            model.displayTime
                        )

                        tvSectionText.text = time
                        tvSectionText.setBackgroundResource(R.drawable.shape_rect_bg_a2a8c3_radius_12)
                        tvSectionText.setTextColor(R.color.white.getColor())
                    }
                } catch (e: Exception) {

                }
                return sectionView
            }

            override fun lastIsUser(position: Int): Boolean? {
                try {
                    val model = onQuick.invoke(position) ?: return null
                    val lastModel = onQuick.invoke(position - 1) ?: return null
                    if (model is TmmMessageVo && lastModel is TmmMessageVo) {
                        return model.uid == lastModel.uid
                    }
                } catch (e: Exception) {
                }
                return null
            }


            override fun getUserImageView(
                position: Int
            ): View? {
//                try {
//                    val avatarView = ChatMessageAvatarView(context)
//                    val model = onQuick.invoke(position) ?: return null
//                    if (model is TmmMessageVo) {
//                        avatarView.binding(model, position)
//                        return avatarView
//                    }
//                } catch (e: Exception) {
//
//                }
                return null
            }

            override fun getSeeHistoryView(
                position: Int
            ): View? {
//                try {
//                    if (isShowHistoryDecoration?.invoke(position) == true) {
//                        return ChatSeeHistoryDecoration(context)
//                    }
//                } catch (e: Exception) {
//
//                }
                return null
            }

            override fun getUserNameView(position: Int): View? {
//                try {
//                    val nameView = ChatDecorationNameView(context)
//                    val model = onQuick.invoke(position) ?: return null
//                    if (model is TmmMessageVo) {
//                        nameView.binding(model, position)
//                        return nameView
//                    }
//                } catch (e: Exception) {
//
//                }
                return null
            }


            override fun getUidView(position: Int): String? {
                try {
                    val model = onQuick.invoke(position) ?: return null
                    if (model is TmmMessageVo) {
                        return model.uid
                    }
                } catch (e: Exception) {

                }
                return null
            }

            override fun isTransMessage(position: Int): Boolean? {
                try {
                    val model = onQuick.invoke(position) ?: return null
                    if (model is TmmMessageVo) {
                        return model.isCenterSender()
                    }
                } catch (e: Exception) {

                }
                return null
            }

            override fun isMeMessage(position: Int): Boolean? {
                try {
                    val model = onQuick.invoke(position) ?: return null
                    if (model is TmmMessageVo) {
                        return model.isOutMessage
                    }
                } catch (e: Exception) {

                }
                return null
            }

            override fun isGroup(position: Int): Boolean? {
                try {
                    val model = onQuick.invoke(position) ?: return null
                    if (model is TmmMessageVo) {
                        return model.isGroup()
                    }
                } catch (e: Exception) {

                }
                return null
            }
        }

        val decoration = ChatStickyDecoration.Builder.init(listener)
            .setGroupHeight(54f.dpToPx())
            .setGroupBackground(R.color.transparent.getColor())
            .setDivideColor(R.color.transparent.getColor())
            .setDivideHeight(12f.dpToPx())
            .setUserDivideHeight(2f.dpToPx())
            .setTransMessageHeight(4f.dpToPx())
            .setNameHeight(15f.dpToPx())
            .setAvatarSize(36f.dpToPx())
            .setCacheEnable(false)
            .build()

        return decoration
    }


}