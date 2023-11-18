package com.learn.worlds.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    object Complete : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    class Error(val errorType: ErrorType = ErrorType.SOMETHING_ERROR) : Result<Nothing>()
}

@Composable
fun stringRes(@StringRes resID: Int?): String {
    return if (resID != null) stringResource(resID) else ""
}

fun LazyListState.isFirstItemVisible() = firstVisibleItemIndex == 0

@Composable
fun LazyListState.isScrollingDown(): Boolean {
    val offset = remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) { derivedStateOf { (firstVisibleItemScrollOffset - offset.intValue) > 0 } }.value
}

@Composable
fun LazyListState.isScrollingUp(): Boolean {
    val offset = remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) { derivedStateOf { (firstVisibleItemScrollOffset - offset.intValue) < 0 } }.value
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

data class DeferrableJob(val dispather: CoroutineDispatcher, val delay: Long = 0L, val action: ()->Unit)

/**
 * An extension that allows you to start the list of deferred coroutines postponed
 * */
@ExperimentalCoroutinesApi
inline fun CoroutineScope.startDelayed(deferrableJobs: List<DeferrableJob>, delay: Long = 0){
    launch(Dispatchers.Default) {
        delay(delay)
        deferrableJobs.forEach {
            delay(it.delay)
            withContext(it.dispather){
                it.action.invoke()
            }
        }
    }
}