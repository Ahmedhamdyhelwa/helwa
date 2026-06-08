package com.helwa.lifemanager.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** أدوات مساعدة للتواريخ والوقت بالعربي */
object DateUtils {

    private val ar = Locale("ar")

    fun greetingFor(hour: Int = currentHour()): String =
        if (hour in 4..11) "صباح الخير" else "مساء الخير"

    fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    /** التاريخ بشكل كامل: الأربعاء 8 يونيو 2026 */
    fun fullDate(time: Long = System.currentTimeMillis()): String =
        SimpleDateFormat("EEEE d MMMM yyyy", ar).format(Date(time))

    /** الوقت بصيغة 12 ساعة: 08:30 ص */
    fun timeOnly(time: Long): String =
        SimpleDateFormat("hh:mm a", ar).format(Date(time))

    /** تاريخ + وقت مختصر */
    fun dateTime(time: Long): String =
        SimpleDateFormat("d MMM - hh:mm a", ar).format(Date(time))

    fun isToday(time: Long): Boolean {
        if (time <= 0L) return false
        val now = Calendar.getInstance()
        val t = Calendar.getInstance().apply { timeInMillis = time }
        return now.get(Calendar.YEAR) == t.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == t.get(Calendar.DAY_OF_YEAR)
    }

    /** يبني وقت في نفس اليوم بساعة ودقيقة محددة */
    fun todayAt(hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    /** أقرب وقت قادم بساعة ودقيقة (لو فات النهاردة يبقى بكرة) */
    fun nextOccurrenceAt(hour: Int, minute: Int): Long {
        val c = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (c.timeInMillis <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1)
        }
        return c.timeInMillis
    }

    /** الوقت القادم بعد إضافة فترة بالدقائق */
    fun afterMinutes(minutes: Int): Long =
        System.currentTimeMillis() + minutes * 60_000L
}
