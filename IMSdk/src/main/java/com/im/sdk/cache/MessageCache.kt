package com.im.sdk.cache

import com.im.sdk.core.utils.SpUtils

object MessageCache {

    fun saveMessageMaxSequence(sequence: Long) {
        SpUtils.putLong("message_max_sequence", sequence)
    }

    fun getMessageMaxSequence(): Long {
        return SpUtils.getLong("message_max_sequence")
    }
}