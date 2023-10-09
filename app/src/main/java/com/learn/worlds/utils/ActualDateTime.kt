package com.learn.worlds.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getCurrentDateTime(format: String = "yyyy/MM/dd HH:mm:ss", locale: Locale = Locale.getDefault()): String {
    val date =  Calendar.getInstance().time

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    return date.toString(format, locale)
}