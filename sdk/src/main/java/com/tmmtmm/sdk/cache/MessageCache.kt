package com.tmmtmm.sdk.cache

import com.tmmtmm.sdk.core.utils.SpUtils

object MessageCache {

    fun saveMessageMaxSequence(sequence: Long) {
        SpUtils.putLong("message_max_sequence", sequence)
    }

    fun getMessageMaxSequence(): Long {
        return SpUtils.getLong("message_max_sequence")
    }
}