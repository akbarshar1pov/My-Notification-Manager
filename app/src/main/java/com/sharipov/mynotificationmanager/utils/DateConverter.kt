package com.sharipov.mynotificationmanager.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun dateConverter(type: String, date: String): Long? {
    if (date.isBlank()) {
        return null
    }

    val parsedDate = runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
    }.getOrNull() ?: return null

    val calendar = Calendar.getInstance().apply {
        time = parsedDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (type == "to") {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
    }

    return calendar.timeInMillis
}
