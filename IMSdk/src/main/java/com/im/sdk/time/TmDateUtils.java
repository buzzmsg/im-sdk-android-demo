package com.im.sdk.time;

import android.content.Context;
import android.text.format.DateFormat;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TmDateUtils extends android.text.format.DateUtils {
    @SuppressWarnings("unused")
    private static final String TAG = TmDateUtils.class.getSimpleName();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat DATE_FORMAT_YYYY_M = new SimpleDateFormat("yyyy-MM");
    public static final SimpleDateFormat DATE_FORMAT_HH_MM_SS_DD_MM_YYYY = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
    public static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD_HH_MM = new SimpleDateFormat(" yyyy-MM-dd HH:mm");

    public static String formatToString(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    public static boolean isWithin(final long millis, final long span, final TimeUnit unit) {
        return System.currentTimeMillis() - millis <= unit.toMillis(span);
    }

    public static boolean isSameYear(final long time) {
        int year = TimeUtils.getValueByCalendarField(time, Calendar.YEAR);
        int currentYear = TimeUtils.getValueByCalendarField(System.currentTimeMillis(), Calendar.YEAR);
        return year == currentYear;
    }

    public static boolean isCurrentMonth(final long time) {
        int month = TimeUtils.getValueByCalendarField(time, Calendar.MONTH);
        int currentMonth = TimeUtils.getValueByCalendarField(System.currentTimeMillis(), Calendar.MONTH);
        return month == currentMonth;
    }

    public static boolean isSameMonth(long t1, long t2) {
        int month1 = TimeUtils.getValueByCalendarField(t1, Calendar.MONTH);
        int month2 = TimeUtils.getValueByCalendarField(t2, Calendar.MONTH);
        return month1 == month2;
    }

    public static boolean isSameNatureWeek(final long time) {
        int weekYear = TimeUtils.getValueByCalendarField(time, Calendar.WEEK_OF_YEAR);
        int currentYear = TimeUtils.getValueByCalendarField(System.currentTimeMillis(), Calendar.WEEK_OF_YEAR);
        return weekYear == currentYear;
    }

    public static boolean isYesterday(final long when) {
        return TmDateUtils.isToday(when + TimeUnit.DAYS.toMillis(1));
    }

    public static int convertDelta(final long millis, TimeUnit to) {
        return (int) to.convert(System.currentTimeMillis() - millis, TimeUnit.MILLISECONDS);
    }

    public static String getFormattedDateTime(long time, String template, Locale locale) {
        final String localizedPattern = getLocalizedPattern(template, locale);
        return new SimpleDateFormat(localizedPattern, locale).format(new Date(time));
    }

    public static String getCurrentTime() {
        final long currentTime = System.currentTimeMillis();
        return getFormattedDateTime(currentTime, "hh:mm", Locale.getDefault());
    }
//
//    public static String getVideoTimeString(long millis) {
////        long durationMs = Util.usToMs(millis);
////        long seconds = new BigDecimal(millis).divide(new BigDecimal(1000)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
////        String standardTime;
////        if (millis <= 0) {
////            standardTime = "00:00";
////        } else if (millis < 60 * 1000) {
////            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds);
////        } else {
////            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
////        }
//        StringBuilder formatBuilder = new StringBuilder();
//        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
//        String standardTime = Util.getStringForTime(formatBuilder, formatter, millis);
//        return standardTime;
//    }


    public static String getChatTimeSpanString(final Context c, final Locale locale, final long timestamp) {
        return getFormattedDateTime(timestamp, "a hh:mm", locale);
    }

    public static String getTimeSpanString(final long timestamp, final String template, final Locale locale) {
        SimpleDateFormat simpleDateFormat;
        java.text.DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
        simpleDateFormat = new SimpleDateFormat(template, locale);
        return simpleDateFormat.format(new Date(timestamp));
    }

//    public static String getExtendedRelativeTimeSpanString(final Context c, final Locale locale, final long timestamp) {
//        if (isWithin(timestamp, 1, TimeUnit.MINUTES)) {
//            return c.getString(R.string.DateUtils_just_now);
//        } else if (isWithin(timestamp, 1, TimeUnit.HOURS)) {
//            int mins = (int) TimeUnit.MINUTES.convert(System.currentTimeMillis() - timestamp, TimeUnit.MILLISECONDS);
//            return mins + " " + c.getResources().getString(R.string.minute);
//        } else {
//            StringBuilder format = new StringBuilder();
//            if (isWithin(timestamp, 6, TimeUnit.DAYS)) format.append("EEE ");
//            else if (isWithin(timestamp, 365, TimeUnit.DAYS)) format.append("MMM d, ");
//            else format.append("MMM d, yyyy, ");
//            if (DateFormat.is24HourFormat(c)) format.append("HH:mm");
//            else format.append("hh:mm a");
//            return getFormattedDateTime(timestamp, format.toString(), locale);
//        }
//    }

    public static String getRelativeTimeSpanString(final Context c, final Locale locale, final long timestamp) {
        StringBuilder format = new StringBuilder();
        if (isWithin(timestamp, 6, TimeUnit.DAYS)) format.append("EEE, ");
        else if (isWithin(timestamp, 365, TimeUnit.DAYS)) format.append("MMM d, ");
        else format.append("MMM d, yyyy, ");
        if (DateFormat.is24HourFormat(c)) format.append("HH:mm:ss");
        else format.append("hh:mm a");
        return getFormattedDateTime(timestamp, format.toString(), locale);
    }


    public static boolean isSameDay(long t1, long t2) {
        return DATE_FORMAT.format(new Date(t1)).equals(DATE_FORMAT.format(new Date(t2)));
    }

//    public static boolean isSameExtendedRelativeTimestamp(@NonNull Context context, @NonNull Locale locale, long t1, long t2) {
//        return getExtendedRelativeTimeSpanString(context, locale, t1).equals(getExtendedRelativeTimeSpanString(context, locale, t2));
//    }

    public static String getLocalizedPattern(String template, Locale locale) {
        return DateFormat.getBestDateTimePattern(locale, template);
    }
}