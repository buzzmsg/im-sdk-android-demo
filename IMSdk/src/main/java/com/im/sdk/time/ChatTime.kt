package com.im.sdk.time

import android.text.format.DateFormat
import com.blankj.utilcode.util.TimeUtils
import com.im.sdk.core.utils.TmUtils
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @description
 * @time 2022/1/11
 * @version
 */
object ChatTime {

    fun getConversationTimeSpanString(timeStamp: Long): String? {
        val is24 = DateFormat.is24HourFormat(TmUtils.sApp)

        when {
            com.im.sdk.time.TmDateUtils.isToday(timeStamp) -> {
                return getTodayConversationTime(is24, timeStamp)
            }

            com.im.sdk.time.TmDateUtils.isYesterday(timeStamp) -> {
                return "Yesterday"
            }

            com.im.sdk.time.TmDateUtils.isSameNatureWeek(timeStamp) -> {
                return com.im.sdk.time.TmDateUtils.getFormattedDateTime(
                    timeStamp,
                    "EEE",
                    Locale.getDefault()
                )
            }

            else -> {
                return getYearConversationTime(timeStamp)
            }
        }
    }

    fun getChatSectionSpanString(timeStamp: Long): String? {
        return when {
            com.im.sdk.time.TmDateUtils.isToday(timeStamp) -> {
                "Today"
            }
            com.im.sdk.time.TmDateUtils.isYesterday(timeStamp) -> {
                "Yesterday"
            }
            com.im.sdk.time.TmDateUtils.isSameNatureWeek(timeStamp) -> {
                com.im.sdk.time.TmDateUtils.getTimeSpanString(
                    timeStamp,
                    "EEE dd.MM",
                    Locale.getDefault()
                )
            }
//            TmDateUtils.isWithin(timestamp, 365, TimeUnit.DAYS) -> {
//                TmDateUtils.getFormattedDateTime(timestamp, "MMM d", locale)
//            }
            else -> {
                return getYearConversationTime(timeStamp)
            }
        }

    }

    fun getChatTimeSpanString(timeStamp: Long, displayTime: Long?): String {
        val is24 = DateFormat.is24HourFormat(TmUtils.sApp)
        return getTodayConversationTime(is24, timeStamp, displayTime)
    }

    fun getTimeZoneOffset(): Long {
        val offset = TimeZone.getDefault().rawOffset
        return TimeUnit.MILLISECONDS.toSeconds(offset.toLong())
    }

    fun getTodayStartTime(): Long {
        val todayString = TimeUtils.getNowString(TimeUtils.getSafeDateFormat("yyyy/MM/dd"))
        val todayStartString = "$todayString 00:00:00"
        return TimeUtils.string2Millis(todayStartString, "yyyy/MM/dd hh:mm:ss")
    }

    private fun getYearConversationTime(timeStamp: Long): String {

        return if (com.im.sdk.time.TmDateUtils.isSameYear(timeStamp)) {
            com.im.sdk.time.TmDateUtils.getTimeSpanString(
                timeStamp,
                "dd.MM",
                Locale.getDefault()
            )
        } else {
            com.im.sdk.time.TmDateUtils.getTimeSpanString(
                timeStamp,
                "dd.MM.yyy",
                Locale.getDefault()
            )
        }
    }

    private fun getTodayConversationTime(
        is24: Boolean,
        timeStamp: Long,
        displayTime: Long? = null
    ): String {
//        val isChina = TmLanguageUtil.isChina()

        var template = if (is24) {
            "HH:mm"
        } else {
            "HH:mm a"
        }

        if (displayTime != null && !com.im.sdk.time.TmDateUtils.isSameDay(timeStamp, displayTime)) {
            template =
                "dd.MM $template"
        }

        return com.im.sdk.time.TmDateUtils.getTimeSpanString(timeStamp, template, Locale.ENGLISH)

    }

}