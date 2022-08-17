@file:JvmName("DateUtil")

package com.ayia.workernotification.util

import java.text.SimpleDateFormat
import java.util.*

fun toDateString(systemTime: Long): String {
    val sdf = SimpleDateFormat("E dd LLL yyyy", Locale.getDefault())
    return sdf.format(systemTime)
}

fun toDateTimeString(systemTime: Long): String {
    val sdf = SimpleDateFormat("E dd LLL yyyy HH:mm aaa", Locale.getDefault())
    return sdf.format(systemTime)
}
fun toTimeString(systemTime: Long): String {

    val sdf = SimpleDateFormat("HH:mm aaa", Locale.getDefault())
    return sdf.format(systemTime)
}

fun toLong(date: Date): Long {
    return date.time
}

fun toDate(timestamp: Long): Date {
    return Date(timestamp)
}

fun toLong(day:Int?, month:Int?, year:Int?): Long{

    val calendar = Calendar.getInstance(Locale.getDefault())
    if (year != null&& month != null && day != null) {
        calendar.set(year, month, day)
    }
    return calendar.timeInMillis
}

fun toCalender(date: Long): Calendar {
    val calendar = Calendar.getInstance(Locale.getDefault())
    calendar.timeInMillis = date
    return calendar
}



private fun getDayPostFix(day: Int): String? {
    var appendix = "th"
    when (day) {
        1, 21, 31 -> appendix = "st"
        2, 22 -> appendix = "nd"
        3, 23 -> appendix = "rd"
    }
    return day.toString() + appendix
}