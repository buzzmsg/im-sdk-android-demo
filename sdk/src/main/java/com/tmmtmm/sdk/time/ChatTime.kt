package com.tmmtmm.sdk.time

import android.text.format.DateFormat
import com.blankj.utilcode.util.TimeUtils
import com.tmmtmm.sdk.R
import com.tmmtmm.sdk.core.net.exception.TmException.Companion.common
import com.tmmtmm.sdk.core.utils.TmUtils
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
            TmDateUtils.isToday(timeStamp) -> {
                return getTodayConversationTime(is24, timeStamp)
            }

            TmDateUtils.isYesterday(timeStamp) -> {
                return "Yesterday"
            }

            TmDateUtils.isSameNatureWeek(timeStamp) -> {
                return TmDateUtils.getFormattedDateTime(timeStamp, "EEE", Locale.getDefault())
            }

            else -> {
                return getYearConversationTime(timeStamp)
            }
        }
    }

//    fun getChatSectionSpanString(timeStamp: Long): String? {
//        return when {
//            TmDateUtils.isToday(timeStamp) -> {
//                TmUtils.sApp.getString(com.tmmtmm.im.style.R.string.DateUtils_today)
//            }
//            TmDateUtils.isYesterday(timeStamp) -> {
//                TmUtils.sApp.getString(com.tmmtmm.im.style.R.string.DateUtils_yesterday)
//            }
//            TmDateUtils.isSameNatureWeek(timeStamp) -> {
//                TmDateUtils.getTimeSpanString(
//                    timeStamp,
//                    TmUtils.sApp.getString(R.string.string_chat_section_within_week_time_template),
//                    Locale.getDefault()
//                )
//            }
////            TmDateUtils.isWithin(timestamp, 365, TimeUnit.DAYS) -> {
////                TmDateUtils.getFormattedDateTime(timestamp, "MMM d", locale)
////            }
//            else -> {
//                return getYearConversationTime(timeStamp)
//            }
//        }
//
//    }

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

        return if (TmDateUtils.isSameYear(timeStamp)) {
            TmDateUtils.getTimeSpanString(
                timeStamp,
                "dd.MM",
                Locale.getDefault()
            )
        } else {
            TmDateUtils.getTimeSpanString(
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

        if (displayTime != null && !TmDateUtils.isSameDay(timeStamp, displayTime)) {
            template =
                "dd.MM $template"
        }

        return TmDateUtils.getTimeSpanString(timeStamp, template, Locale.ENGLISH)

    }

}