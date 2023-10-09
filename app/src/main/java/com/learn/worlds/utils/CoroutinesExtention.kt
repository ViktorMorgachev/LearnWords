package com.learn.worlds.utils

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface Request<T> {
    fun execute(callback: Callback<T>)
    fun cancel()

    interface Callback<T> {
        fun onSuccess(value: T)
        fun onError(t: Throwable)
    }
}

interface Operation<T> {
    fun performAsync(callback: (T?, Throwable?) -> Unit)
}

suspend fun <T> Operation<T>.perform(): T =
    suspendCoroutine { continuation ->
        performAsync { value, exception ->
            when {
                exception != null ->
                    continuation.resumeWithException(exception)
                else -> // succeeded, there is a value
                    continuation.resume(value as T)
            }
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

