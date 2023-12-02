package com.learn.worlds.utils

import java.util.Locale


fun <T> List<T>.update(item: T): List<T> {
    val itemIndex = indexOf(item)
    return if (itemIndex == -1) this.toList()
    else slice(0 until itemIndex) + item + slice(itemIndex+1 until size)
}

fun String.capitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}
