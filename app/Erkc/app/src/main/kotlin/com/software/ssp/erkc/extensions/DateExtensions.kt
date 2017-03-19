package com.software.ssp.erkc.extensions

import java.text.SimpleDateFormat
import java.util.*


fun Date.toString(format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = this
    cal2.time = date

    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}