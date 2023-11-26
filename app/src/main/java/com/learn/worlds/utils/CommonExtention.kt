package com.learn.worlds.utils


fun <T> List<T>.update(item: T): List<T> {
    val itemIndex = indexOf(item)
    return if (itemIndex == -1) this.toList()
    else slice(0 until itemIndex) + item + slice(itemIndex+1 until size)
}