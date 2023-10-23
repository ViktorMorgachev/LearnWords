package com.learn.worlds.utils

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

interface Request<T> {
    fun execute(callback: Callback<T>)
    fun cancel()

    interface Callback<T> {
        fun onSuccess(value: T)
        fun onError(t: Throwable)
    }
}

fun <T> Request<T>.asFlow() = callbackFlow {
        execute(object : Request.Callback<T> {
            override fun onSuccess(value: T) {
                trySendBlocking(value)
                close()
            }

            override fun onError(t: Throwable) {
                close(t)
            }

        })
        awaitClose { this@asFlow.cancel() }
}

