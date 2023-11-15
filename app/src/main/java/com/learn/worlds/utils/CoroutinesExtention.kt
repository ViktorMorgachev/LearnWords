package com.learn.worlds.utils

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


inline fun <T> Continuation<T>.safeResume(value: T, onExceptionCalled: () -> Unit = {Timber.d("Job has already done")}) {
    if (this is CancellableContinuation) {
        if (isActive)
            resume(value)
        else
            onExceptionCalled()
    } else throw Exception("Must use suspendCancellableCoroutine instead of suspendCoroutine")
}

// Only for others project, for this doesn't actual, maybe later
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

suspend fun <T> MutableSharedFlow<T>.emitIf(value: T, condition: ()->Boolean){
    if (condition.invoke()){
        emit(value)
    }
}

