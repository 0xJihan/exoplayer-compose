package com.jihan.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(this)
    return dateFormat.format(date)

}

fun Long.toDurationString(): String {
    val hours = this / 3600000
    val minutes = (this % 3600000) / 60000
    val seconds = (this % 60000) / 1000

    return buildString {
        if (hours > 0) append("${hours} h ")
        if (minutes > 0) append("${minutes} min ")
        if (seconds > 0 || isEmpty()) append("${seconds} sec")
    }.trim()
}
