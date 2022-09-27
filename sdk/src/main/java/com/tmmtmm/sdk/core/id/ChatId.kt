package com.tmmtmm.sdk.core.id

import android.text.TextUtils
import android.util.Log
import com.tmmtmm.sdk.logic.TmLoginLogic

import com.tmmtmm.sdk.core.hash.MD5
import com.tmmtmm.sdk.core.utils.Random


/**
 * @description
 *
 * @time 2021/5/13 11:02 上午
 * @version
 */
class ChatId private constructor(private var code: String) {

    companion object {

        private const val TAG = "ChatId"

        private const val SINGLE_PREFIX = "s"

        private const val GROUP_PREFIX = "g"

        private const val SEPARATOR = "_"

        private const val GROUP_PARAM = "group"

        fun isSingle(chatId: String): Boolean {
            return chatId.startsWith(SINGLE_PREFIX + SEPARATOR)
        }

        fun getTargetId(chatId: String): String {
            if (!isSingle(chatId)) {
                return ""
            }

            val split = chatId.split(SEPARATOR)
            if (split.size < 3) {
                Log.d(TAG, "splitUid() error, code =${chatId}")
                return ""
            }

            return if (!TextUtils.equals(split[1], TmLoginLogic.getInstance().getUserId())) {
                split[1]
            } else if (!TextUtils.equals(split[2], TmLoginLogic.getInstance().getUserId())) {
                split[2]
            } else if (TextUtils.equals(
                    split[1],
                    TmLoginLogic.getInstance().getUserId()
                ) && TextUtils.equals(split[2], TmLoginLogic.getInstance().getUserId())
            ) {
                TmLoginLogic.getInstance().getUserId()
            } else {
                ""
            }
        }

        fun createSingle(uid1: String, uid2: String): ChatId {
            val mSingleChatId = if (uid1 > uid2) {
                "$SINGLE_PREFIX$SEPARATOR$uid2$SEPARATOR$uid1"
            } else {
                "$SINGLE_PREFIX$SEPARATOR$uid1$SEPARATOR$uid2"
            }
            return ChatId(mSingleChatId)
        }

        fun createById(chatId: String) = ChatId(chatId)

        fun createGroup(uid: String): ChatId {
            val mGroupChatId = "$uid${System.currentTimeMillis()}${Random.create(6)}$GROUP_PARAM"
            return ChatId("$GROUP_PREFIX$SEPARATOR${MD5.create(mGroupChatId)}")
        }
    }

    fun isChatId(): Boolean {
        if (code.isBlank()) {
            return false
        }

        return code.startsWith(GROUP_PREFIX + SEPARATOR) || code.startsWith(SINGLE_PREFIX + SEPARATOR)
    }

    fun isSingle() = code.startsWith(SINGLE_PREFIX + SEPARATOR)

    fun isGroup() = code.startsWith(GROUP_PREFIX + SEPARATOR)

    fun encode() = code

    fun getTargetId(): String {
        if (!isSingle()) {
            return ""
        }

        val split = code.split(SEPARATOR)
        if (split.size < 3) {
            Log.d(TAG, "splitUid() error, code =${code}")
            return ""
        }

        return if (!TextUtils.equals(split[1], TmLoginLogic.getInstance().getUserId())) {
            split[1]
        } else if (!TextUtils.equals(split[2], TmLoginLogic.getInstance().getUserId())) {
            split[2]
        } else if (TextUtils.equals(
                split[1],
                TmLoginLogic.getInstance().getUserId()
            ) && TextUtils.equals(split[2], TmLoginLogic.getInstance().getUserId())
        ) {
            TmLoginLogic.getInstance().getUserId()
        } else {
            ""
        }
    }

    fun splitUid(): List<String>? {
        if (!isSingle()) {
            return null
        }

        val split = code.split(SEPARATOR)
        if (split.size != 3) {
            Log.d(TAG, "splitUid() error, code =${code}")
            return null
        }
        return mutableListOf(split[1], split[2])
    }
}