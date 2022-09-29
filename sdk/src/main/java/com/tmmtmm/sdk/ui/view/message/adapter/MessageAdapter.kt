package com.tmmtmm.sdk.ui.view.message.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.tmmtmm.sdk.R
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.databinding.ReceivedMessageItemViewBinding
import com.tmmtmm.sdk.databinding.SenderMessageItemViewBinding
import com.tmmtmm.sdk.time.ChatTime
import com.tmmtmm.sdk.ui.ext.Stub
import com.tmmtmm.sdk.ui.ext.getColor
import com.tmmtmm.sdk.ui.ext.gone
import com.tmmtmm.sdk.ui.ext.visible
import com.tmmtmm.sdk.ui.view.link.textview.MessageTextLinkView
import com.tmmtmm.sdk.ui.view.message.diff.MessageDiff
import com.tmmtmm.sdk.ui.view.vo.TmmMessageVo

/**
 * @description
 * @version
 */
class MessageAdapter() : BaseMultiItemAdapter<TmmMessageVo>() {
    internal class ItemTextReceiveMessageVH(viewBinding: ReceivedMessageItemViewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        var viewBinding: ReceivedMessageItemViewBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            ReceivedMessageItemViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        ) {
        }
    }


    internal class ItemTextSenderMessageVH(viewBinding: SenderMessageItemViewBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        var viewBinding: SenderMessageItemViewBinding? = null

        init {
            this.viewBinding = viewBinding
        }

        constructor(parent: ViewGroup) : this(
            SenderMessageItemViewBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        ) {
        }
    }


    init {
        addItemType(
            MessageContentType.ContentType_Text,
            ItemTextReceiveMessageVH::class.java,
            object : OnMultiItemAdapterListener<TmmMessageVo, ItemTextReceiveMessageVH> {
                override fun onBind(
                    holder: ItemTextReceiveMessageVH,
                    position: Int,
                    message: TmmMessageVo?
                ) {

                    
                    val stubQuoteMessage =
                        Stub<LinearLayoutCompat>(holder.viewBinding?.stubQuoteMessage)



//                    if (message?.quoteMessageVo?.tmmMessageVo != null) {
//                        bindQuoteMessage(message)
//                    } else 
                    if (stubQuoteMessage.isInflate) {
                        val layoutQuoteMessageView = stubQuoteMessage.get()
                            ?.findViewById<LinearLayoutCompat>(R.id.layoutQuoteMessage)
                        layoutQuoteMessageView?.gone()
                    }

                    if (message?.isOutMessage == true) {
                        holder.viewBinding?.messageContainerView?.setPadding(0, 0, 0, 0)
                    } else {
                        holder.viewBinding?.messageContainerView?.setPadding(0, 0, 0, 0)
                    }


                    if (message?.isOutMessage == false) {
                        val inColor = ContextCompat.getColor(context, R.color.text_0D1324)
                        val linkColor = ContextCompat.getColor(context, R.color.theme_color_blue)
                        holder.viewBinding?.messageTextContentTv?.setLinkTextColor(linkColor)
                        holder.viewBinding?.messageTextContentTv?.setTextColor(inColor)
//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(
//                            R.color.text_in_message_back_color.getColor()
//                        )

                        holder.viewBinding?.messageTimeTv?.setTextColor(R.color.text_A2A8C3.getColor())
                        holder.viewBinding?.messageContainerView?.setBackgroundResource(R.drawable.bg_receive_text_message_out_line)

                    } else {
                        val outColor = ContextCompat.getColor(context, R.color.text_0D1324)
                        val linkColor = ContextCompat.getColor(context, R.color.theme_color_blue)
                        holder.viewBinding?.messageTextContentTv?.setLinkTextColor(linkColor)
                        holder.viewBinding?.messageTextContentTv?.setTextColor(outColor)

//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(R.color.c_dfe6.getColor())



                        holder.viewBinding?.messageTimeTv?.setTextColor(R.color.text_0D1324_30.getColor())
                        holder.viewBinding?.messageContainerView?.setBackgroundResource(R.drawable.bg_sender_text_message_out_line)
                    }



                    val date = ChatTime.getChatTimeSpanString(
                        if (message?.isOutMessage == true ) message.createTime else message?.sendTime ?:0 ,
                        displayTime = message?.displayTime
                    )

                    

                    val content = if (message?.isOutMessage == true) {

                        SpanUtils().append(message.messageBody ?: "")
                            .append(" " + date + "1")
                            .setForegroundColor(R.color.transparent.getColor())
                            .create()
                    } else {
                        SpanUtils().append(message?.messageBody ?: "")
                            .append(" " + date.substring(0, date.length - 1))
                            .setForegroundColor(R.color.transparent.getColor())
                            .create()
                    }

                    holder.viewBinding?.messageTextContentTv?.text = content


                    holder.viewBinding?.messageTimeTv?.text = date
                    
                    if (message?.isOutMessage == true) {
                        holder.viewBinding?.messageStatusImg?.visible()
                        holder.viewBinding?.messageStatusImg?.showMessageStatus(message.status)
                    } else {
                        holder.viewBinding?.messageStatusImg?.gone()
                    }

                    holder.viewBinding?.messageTextContentTv?.setOnLinkLongClickListener {

                    }
                    holder.viewBinding?.messageTextContentTv?.setOnLinkClickListener(object :
                            MessageTextLinkView.OnLinkClickListener {
                            override fun onTelLinkClick(phoneNumber: String?) {

                            }

                            override fun onMailLinkClick(mailAddress: String?) {

                            }

                            override fun onWebUrlLinkClick(url: String?) {
//                                WebViewActivity.starter(context, url ?: "", shareVisible = false)
                            }
                        })
                    

                    

                }

                override fun onBind(
                    holder: ItemTextReceiveMessageVH,
                    position: Int,
                    item: TmmMessageVo?,
                    payloads: List<Any>
                ) {
                    super.onBind(holder, position, item, payloads)

                    if (payloads.isEmpty()) {
                        return
                    }

                    val payload = payloads[0]
                    if (payload !is Bundle) {
                        return
                    }
                    for (key in payload.keySet()) {
                        when (key) {
                            MessageDiff.KEY_MESSAGE_STATUS -> {
                                val messageStatus = payload.getInt(MessageDiff.KEY_MESSAGE_STATUS, 0)
                                if (item?.isOutMessage == true) {
                                    holder.viewBinding?.messageStatusImg?.visible()
                                    holder.viewBinding?.messageStatusImg?.showMessageStatus(messageStatus)
                                } else {
                                    holder.viewBinding?.messageStatusImg?.gone()
                                }
                            }

                            MessageDiff.KEY_MESSAGE_SEND_TIME -> {
                                val sendTime = payload.getLong(MessageDiff.KEY_MESSAGE_SEND_TIME, 0)
                                val date = ChatTime.getChatTimeSpanString(
                                    if (item?.isOutMessage == true ) item.createTime else sendTime ,
                                    displayTime = item?.displayTime
                                )
                                holder.viewBinding?.messageTimeTv?.text = date
                            }
                        }
                    }
                }

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): ItemTextReceiveMessageVH {
                    return ItemTextReceiveMessageVH(parent)
                }
            })

        addItemType(
            0 - MessageContentType.ContentType_Text,
            ItemTextSenderMessageVH::class.java,
            object : OnMultiItemAdapterListener<TmmMessageVo, ItemTextSenderMessageVH> {
                override fun onBind(
                    holder: ItemTextSenderMessageVH,
                    position: Int,
                    message: TmmMessageVo?
                ) {
                    val stubQuoteMessage =
                        Stub<LinearLayoutCompat>(holder.viewBinding?.stubQuoteMessage)



//                    if (message?.quoteMessageVo?.tmmMessageVo != null) {
//                        bindQuoteMessage(message)
//                    } else
                    if (stubQuoteMessage.isInflate) {
                        val layoutQuoteMessageView = stubQuoteMessage.get()
                            ?.findViewById<LinearLayoutCompat>(R.id.layoutQuoteMessage)
                        layoutQuoteMessageView?.gone()
                    }

                    if (message?.isOutMessage == true) {
                        holder.viewBinding?.messageContainerView?.setPadding(0, 0, 0, 0)
                    } else {
                        holder.viewBinding?.messageContainerView?.setPadding(0, 0, 0, 0)
                    }


                    if (message?.isOutMessage == false) {
                        val inColor = ContextCompat.getColor(context, R.color.text_0D1324)
                        val linkColor = ContextCompat.getColor(context, R.color.theme_color_blue)
                        holder.viewBinding?.messageTextContentTv?.setLinkTextColor(linkColor)
                        holder.viewBinding?.messageTextContentTv?.setTextColor(inColor)
//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(
//                            R.color.text_in_message_back_color.getColor()
//                        )

                        holder.viewBinding?.messageTimeTv?.setTextColor(R.color.text_A2A8C3.getColor())
                        holder.viewBinding?.messageContainerView?.setBackgroundResource(R.drawable.bg_receive_text_message_out_line)

                    } else {
                        val outColor = ContextCompat.getColor(context, R.color.text_0D1324)
                        val linkColor = ContextCompat.getColor(context, R.color.theme_color_blue)
                        holder.viewBinding?.messageTextContentTv?.setLinkTextColor(linkColor)
                        holder.viewBinding?.messageTextContentTv?.setTextColor(outColor)

//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(R.color.c_dfe6.getColor())



                        holder.viewBinding?.messageTimeTv?.setTextColor(R.color.text_0D1324_30.getColor())
                        holder.viewBinding?.messageContainerView?.setBackgroundResource(R.drawable.bg_sender_text_message_out_line)
                    }



                    val date = ChatTime.getChatTimeSpanString(
                        if (message?.isOutMessage == true ) message.createTime else message?.sendTime ?:0 ,
                        displayTime = message?.displayTime
                    )



                    val content = if (message?.isOutMessage == true) {

                        SpanUtils().append(message.messageBody ?: "")
                            .append(" " + date + "1")
                            .setForegroundColor(R.color.transparent.getColor())
                            .create()
                    } else {
                        SpanUtils().append(message?.messageBody ?: "")
                            .append(" " + date.substring(0, date.length - 1))
                            .setForegroundColor(R.color.transparent.getColor())
                            .create()
                    }

                    holder.viewBinding?.messageTextContentTv?.text = content


                    holder.viewBinding?.messageTimeTv?.text = date

                    if (message?.isOutMessage == true) {
                        holder.viewBinding?.messageStatusImg?.visible()
                        holder.viewBinding?.messageStatusImg?.showMessageStatus(message.status)
                    } else {
                        holder.viewBinding?.messageStatusImg?.gone()
                    }

                    holder.viewBinding?.messageTextContentTv?.setOnLinkLongClickListener {

                    }
                    holder.viewBinding?.messageTextContentTv?.setOnLinkClickListener(object :
                        MessageTextLinkView.OnLinkClickListener {
                        override fun onTelLinkClick(phoneNumber: String?) {

                        }

                        override fun onMailLinkClick(mailAddress: String?) {

                        }

                        override fun onWebUrlLinkClick(url: String?) {
//                                WebViewActivity.starter(context, url ?: "", shareVisible = false)
                        }
                    })




                }

                override fun onBind(
                    holder: ItemTextSenderMessageVH,
                    position: Int,
                    item: TmmMessageVo?,
                    payloads: List<Any>
                ) {
                    super.onBind(holder, position, item, payloads)

                    if (payloads.isEmpty()) {
                        return
                    }

                    val payload = payloads[0]
                    if (payload !is Bundle) {
                        return
                    }
                    for (key in payload.keySet()) {
                        when (key) {
                            MessageDiff.KEY_MESSAGE_STATUS -> {
                                val messageStatus = payload.getInt(MessageDiff.KEY_MESSAGE_STATUS, 0)
                                if (item?.isOutMessage == true) {
                                    holder.viewBinding?.messageStatusImg?.visible()
                                    holder.viewBinding?.messageStatusImg?.showMessageStatus(messageStatus)
                                } else {
                                    holder.viewBinding?.messageStatusImg?.gone()
                                }
                            }

                            MessageDiff.KEY_MESSAGE_SEND_TIME -> {
                                val sendTime = payload.getLong(MessageDiff.KEY_MESSAGE_SEND_TIME, 0)
                                val date = ChatTime.getChatTimeSpanString(
                                    if (item?.isOutMessage == true ) item.createTime else sendTime ,
                                    displayTime = item?.displayTime
                                )
                                holder.viewBinding?.messageTimeTv?.text = date
                            }
                        }
                    }
                }

                override fun onCreate(
                    context: Context,
                    parent: ViewGroup,
                    viewType: Int
                ): ItemTextSenderMessageVH {
                    return ItemTextSenderMessageVH(parent)
                }
            })
            .onItemViewType { position, list -> list[position].getItemType() }
    }

}