package com.tmmtmm.sdk.ui.view.message.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseMultiItemAdapter;
import com.tmmtmm.sdk.R;
import com.tmmtmm.sdk.constant.MessageContentType;
import com.tmmtmm.sdk.databinding.ReceivedMessageItemViewBinding;
import com.tmmtmm.sdk.databinding.SenderMessageItemViewBinding;
import com.tmmtmm.sdk.time.ChatTime;
import com.tmmtmm.sdk.ui.view.message.diff.MessageDiff;
import com.tmmtmm.sdk.ui.view.vo.TmmMessageVo;

import java.util.List;

/**
 * @description
 */
public class MessageMultiAdapter extends BaseMultiItemAdapter<TmmMessageVo> {
    static class ItemTextReceiveMessageVH extends RecyclerView.ViewHolder {

        ReceivedMessageItemViewBinding viewBinding;

        public ItemTextReceiveMessageVH(@NonNull ReceivedMessageItemViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        public ItemTextReceiveMessageVH(@NonNull ViewGroup parent) {
            this(ReceivedMessageItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    static class ItemTextSenderMessageVH extends RecyclerView.ViewHolder {

        SenderMessageItemViewBinding viewBinding;

        public ItemTextSenderMessageVH(@NonNull SenderMessageItemViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        public ItemTextSenderMessageVH(@NonNull ViewGroup parent) {
            this(SenderMessageItemViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }


    public MessageMultiAdapter(List<TmmMessageVo> data) {
        super(data);

        addItemType(
                MessageContentType.ContentType_Text,
                ItemTextReceiveMessageVH.class,
                new OnMultiItemAdapterListener<TmmMessageVo, ItemTextReceiveMessageVH>() {
                    @NonNull
                    @Override
                    public ItemTextReceiveMessageVH onCreate(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
                        return new ItemTextReceiveMessageVH(viewGroup);
                    }

                    @Override
                    public void onBind(@NonNull ItemTextReceiveMessageVH holder, int i, @Nullable TmmMessageVo message) {


//                    val stubQuoteMessage =
//                        Stub<LinearLayoutCompat>(holder.viewBinding?.stubQuoteMessage)
//
//
//
////                    if (message?.quoteMessageVo?.tmmMessageVo != null) {
////                        bindQuoteMessage(message)
////                    } else
//                    if (stubQuoteMessage.isInflate) {
//                        val layoutQuoteMessageView = stubQuoteMessage.get()
//                            ?.findViewById<LinearLayoutCompat>(R.id.layoutQuoteMessage)
//                        layoutQuoteMessageView?.gone()
//                    }

                        if (message.isOutMessage()) {
                            holder.viewBinding.messageContainerView.setPadding(0, 0, 0, 0);
                        } else {
                            holder.viewBinding.messageContainerView.setPadding(0, 0, 0, 0);
                        }


                        if (!message.isOutMessage()) {
                            int inColor = ContextCompat.getColor(getContext(), R.color.text_0D1324);
                            int linkColor = ContextCompat.getColor(getContext(), R.color.theme_color_blue);
                            holder.viewBinding.messageTextContentTv.setLinkTextColor(linkColor);
                            holder.viewBinding.messageTextContentTv.setTextColor(inColor);
//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(
//                            R.color.text_in_message_back_color.getColor()
//                        )

                            holder.viewBinding.messageTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_A2A8C3));
                            holder.viewBinding.messageContainerView.setBackgroundResource(R.drawable.bg_receive_text_message_out_line);

                        } else {
                            int outColor = ContextCompat.getColor(getContext(), R.color.text_0D1324);
                            int linkColor = ContextCompat.getColor(getContext(), R.color.theme_color_blue);
                            holder.viewBinding.messageTextContentTv.setLinkTextColor(linkColor);
                            holder.viewBinding.messageTextContentTv.setTextColor(outColor);

//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(R.color.c_dfe6.getColor())


                            holder.viewBinding.messageTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_0D1324_30));
                            holder.viewBinding.messageContainerView.setBackgroundResource(R.drawable.bg_sender_text_message_out_line);
                        }


                        long time = 0L;
                        if (message.isOutMessage()) {
                            time = message.getCreateTime();
                        } else {
                            time = message.getSendTime();
                        }
                        String date = ChatTime.INSTANCE.getChatTimeSpanString(
                                time,
                                message.getDisplayTime()
                        );


                        if (message.isOutMessage()) {

                            SpannableStringBuilder content = new SpanUtils().append(message.getMessageBody())
                                    .append(" " + date + "1")
                                    .setForegroundColor(ContextCompat.getColor(getContext(), R.color.transparent))
                                    .create();

                            holder.viewBinding.messageTextContentTv.setText(content);
                        } else {
                            SpannableStringBuilder content = new SpanUtils().append(message.getMessageBody())
                                    .append(" " + date.substring(0, date.length() - 1))
                                    .setForegroundColor(ContextCompat.getColor(getContext(), R.color.transparent))
                                    .create();
                            holder.viewBinding.messageTextContentTv.setText(content);
                        }


                        holder.viewBinding.messageTimeTv.setText(date);

                        if (message.isOutMessage()) {
                            holder.viewBinding.messageStatusImg.setVisible();
                            holder.viewBinding.messageStatusImg.showMessageStatus(message.getStatus());
                        } else {
                            holder.viewBinding.messageStatusImg.setGone();
                        }

//                        holder.viewBinding .messageTextContentTv.setOnLinkLongClickListener {
//
//                        }
//                        holder.viewBinding ?.messageTextContentTv ?.setOnLinkClickListener(object :
//                        MessageTextLinkView.OnLinkClickListener {
//                            override fun onTelLinkClick(phoneNumber:String ?){
//
//                            }
//
//                            override fun onMailLinkClick(mailAddress:String ?){
//
//                            }
//
//                            override fun onWebUrlLinkClick(url:String ?){
////                                WebViewActivity.starter(context, url ?: "", shareVisible = false)
//                            }
//                        })


                    }


                    @Override
                    public void onBind(@NonNull ItemTextReceiveMessageVH holder, int position, @Nullable TmmMessageVo item, @NonNull List<?> payloads) {
                        OnMultiItemAdapterListener.super.onBind(holder, position, item, payloads);
                        if (payloads.isEmpty()) {
                            return;
                        }

                        Object payload = payloads.get(0);
                        if (!(payload instanceof Bundle)) {
                            return;
                        }

                        for (String key : ((Bundle) payload).keySet()) {
                            switch (key) {
                                case MessageDiff.KEY_MESSAGE_STATUS: {
                                    int messageStatus = ((Bundle) payload).getInt(MessageDiff.KEY_MESSAGE_STATUS, 0);
                                    if (item != null && item.isOutMessage()) {
                                        holder.viewBinding.messageStatusImg.setVisible();
                                        holder.viewBinding.messageStatusImg.showMessageStatus(messageStatus);
                                    } else {
                                        holder.viewBinding.messageStatusImg.setGone();
                                    }

                                    break;
                                }

                                case MessageDiff.KEY_MESSAGE_SEND_TIME: {
                                    long sendTime = ((Bundle) payload).getLong(MessageDiff.KEY_MESSAGE_SEND_TIME, 0);

                                    long time = 0L;
                                    if (item.isOutMessage()) {
                                        time = item.getCreateTime();
                                    } else {
                                        time = sendTime;
                                    }
                                    String date = ChatTime.INSTANCE.getChatTimeSpanString(
                                            time,
                                            item.getDisplayTime()
                                    );
                                    holder.viewBinding.messageTimeTv.setText(date);
                                }
                            }
                        }
                    }
                })
                .addItemType(
                        0 - MessageContentType.ContentType_Text,
                        ItemTextSenderMessageVH.class,
                        new OnMultiItemAdapterListener<TmmMessageVo, ItemTextSenderMessageVH>() {
                            @NonNull
                            @Override
                            public ItemTextSenderMessageVH onCreate(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
                                return new ItemTextSenderMessageVH(viewGroup);
                            }

                            @Override
                            public void onBind(@NonNull ItemTextSenderMessageVH holder, int i, @Nullable TmmMessageVo message) {


//                    val stubQuoteMessage =
//                        Stub<LinearLayoutCompat>(holder.viewBinding?.stubQuoteMessage)
//
//
//
////                    if (message?.quoteMessageVo?.tmmMessageVo != null) {
////                        bindQuoteMessage(message)
////                    } else
//                    if (stubQuoteMessage.isInflate) {
//                        val layoutQuoteMessageView = stubQuoteMessage.get()
//                            ?.findViewById<LinearLayoutCompat>(R.id.layoutQuoteMessage)
//                        layoutQuoteMessageView?.gone()
//                    }

                                if (message.isOutMessage()) {
                                    holder.viewBinding.messageContainerView.setPadding(0, 0, 0, 0);
                                } else {
                                    holder.viewBinding.messageContainerView.setPadding(0, 0, 0, 0);
                                }


                                if (!message.isOutMessage()) {
                                    int inColor = ContextCompat.getColor(getContext(), R.color.text_0D1324);
                                    int linkColor = ContextCompat.getColor(getContext(), R.color.theme_color_blue);
                                    holder.viewBinding.messageTextContentTv.setLinkTextColor(linkColor);
                                    holder.viewBinding.messageTextContentTv.setTextColor(inColor);
//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(
//                            R.color.text_in_message_back_color.getColor()
//                        )

                                    holder.viewBinding.messageTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_A2A8C3));
                                    holder.viewBinding.messageContainerView.setBackgroundResource(R.drawable.bg_receive_text_message_out_line);

                                } else {
                                    int outColor = ContextCompat.getColor(getContext(), R.color.text_0D1324);
                                    int linkColor = ContextCompat.getColor(getContext(), R.color.theme_color_blue);
                                    holder.viewBinding.messageTextContentTv.setLinkTextColor(linkColor);
                                    holder.viewBinding.messageTextContentTv.setTextColor(outColor);

//                        holder.viewBinding?.messageContainerView?.setMessageBackgroundColor(R.color.c_dfe6.getColor())


                                    holder.viewBinding.messageTimeTv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_0D1324_30));
                                    holder.viewBinding.messageContainerView.setBackgroundResource(R.drawable.bg_sender_text_message_out_line);
                                }


                                long time = 0L;
                                if (message.isOutMessage()) {
                                    time = message.getCreateTime();
                                } else {
                                    time = message.getSendTime();
                                }
                                String date = ChatTime.INSTANCE.getChatTimeSpanString(
                                        time,
                                        message.getDisplayTime()
                                );


                                if (message.isOutMessage()) {

                                    SpannableStringBuilder content = new SpanUtils().append(message.getMessageBody())
                                            .append(" " + date + "1")
                                            .setForegroundColor(ContextCompat.getColor(getContext(), R.color.transparent))
                                            .create();

                                    holder.viewBinding.messageTextContentTv.setText(content);
                                } else {
                                    SpannableStringBuilder content = new SpanUtils().append(message.getMessageBody())
                                            .append(" " + date.substring(0, date.length() - 1))
                                            .setForegroundColor(ContextCompat.getColor(getContext(), R.color.transparent))
                                            .create();
                                    holder.viewBinding.messageTextContentTv.setText(content);
                                }


                                holder.viewBinding.messageTimeTv.setText(date);

                                if (message.isOutMessage()) {
                                    holder.viewBinding.messageStatusImg.setVisible();
                                    holder.viewBinding.messageStatusImg.showMessageStatus(message.getStatus());
                                } else {
                                    holder.viewBinding.messageStatusImg.setGone();
                                }

//                        holder.viewBinding .messageTextContentTv.setOnLinkLongClickListener {
//
//                        }
//                        holder.viewBinding ?.messageTextContentTv ?.setOnLinkClickListener(object :
//                        MessageTextLinkView.OnLinkClickListener {
//                            override fun onTelLinkClick(phoneNumber:String ?){
//
//                            }
//
//                            override fun onMailLinkClick(mailAddress:String ?){
//
//                            }
//
//                            override fun onWebUrlLinkClick(url:String ?){
////                                WebViewActivity.starter(context, url ?: "", shareVisible = false)
//                            }
//                        })


                            }


                            @Override
                            public void onBind(@NonNull ItemTextSenderMessageVH holder, int position, @Nullable TmmMessageVo item, @NonNull List<?> payloads) {
                                OnMultiItemAdapterListener.super.onBind(holder, position, item, payloads);
                                if (payloads.isEmpty()) {
                                    return;
                                }

                                Object payload = payloads.get(0);
                                if (!(payload instanceof Bundle)) {
                                    return;
                                }

                                for (String key : ((Bundle) payload).keySet()) {
                                    switch (key) {
                                        case MessageDiff.KEY_MESSAGE_STATUS: {
                                            int messageStatus = ((Bundle) payload).getInt(MessageDiff.KEY_MESSAGE_STATUS, 0);
                                            if (item != null && item.isOutMessage()) {
                                                holder.viewBinding.messageStatusImg.setVisible();
                                                holder.viewBinding.messageStatusImg.showMessageStatus(messageStatus);
                                            } else {
                                                holder.viewBinding.messageStatusImg.setGone();
                                            }

                                            break;
                                        }

                                        case MessageDiff.KEY_MESSAGE_SEND_TIME: {
                                            long sendTime = ((Bundle) payload).getLong(MessageDiff.KEY_MESSAGE_SEND_TIME, 0);

                                            long time = 0L;
                                            if (item.isOutMessage()) {
                                                time = item.getCreateTime();
                                            } else {
                                                time = sendTime;
                                            }
                                            String date = ChatTime.INSTANCE.getChatTimeSpanString(
                                                    time,
                                                    item.getDisplayTime()
                                            );
                                            holder.viewBinding.messageTimeTv.setText(date);
                                        }
                                    }
                                }
                            }
                        }
                )
                .onItemViewType(new OnItemViewTypeListener<TmmMessageVo>() {
                    @Override
                    public int onItemViewType(int position, @NonNull List<? extends TmmMessageVo> list) {
                        return list.get(position).getItemType();
                    }
                });

    }

}
