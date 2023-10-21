package com.learn.worlds.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.learn.worlds.data.model.remote.LearningItemAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

sealed class Result<out T> {
    object Loading : Result<Nothing>()

    object Complete : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    class Error(val error: String? = null) : Result<Nothing>()
}

@Composable
fun stringRes(@StringRes resID: Int?): String {
    return if (resID != null) stringResource(resID) else ""
}

@Composable
fun <T> Flow<T>.flowWithLifecycleStateInAndCollectAsState(
    scope: CoroutineScope,
    initial: T? = null,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T?> {
    val lifecycleOwner = LocalLifecycleOwner.current
    return remember(this, lifecycleOwner) {
        this
            .flowWithLifecycle(
                lifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initial
            )
    }.collectAsState(context)
}