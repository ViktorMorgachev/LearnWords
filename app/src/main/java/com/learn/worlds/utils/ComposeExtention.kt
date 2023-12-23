package com.learn.worlds.utils

import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
import timber.log.Timber
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

fun LazyListState.allItemsVisible() = layoutInfo.visibleItemsInfo.size == layoutInfo.totalItemsCount


fun LazyListState.isSmallScrolledUp(): Boolean?{
    with(layoutInfo){
        visibleItemsInfo.firstOrNull()?.let {
            return  viewportStartOffset - it.offset > 0
        }
        return null
    }
}

fun LazyListState.isScrolledToEnd(): Boolean? {
    with(layoutInfo){
        visibleItemsInfo.lastOrNull()?.let {
            return  viewportEndOffset - it.offset == it.size
        }
        return null
    }
}

fun LazyListState.isScrolledToStart(): Boolean? {
    with(layoutInfo){
        visibleItemsInfo.firstOrNull()?.let {
            return  viewportStartOffset - it.offset == 0
        }
        return null
    }
}

@Composable
fun ShimmerSpacer(
    modifier: Modifier,
    shape: Shape
){
    val colors = listOf(
        MaterialTheme.colorScheme.onBackground,
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.onBackground
    )

    BoxWithConstraints {

        // get max width of the box
        val spaceMaxWidth = with(LocalDensity.current){
            maxWidth.toPx()
        }

        val spaceMaxHeight = with(LocalDensity.current){
            maxHeight.toPx()
        }

        val shimmerWidthPercentage = 0.4

        val transition = rememberInfiniteTransition()

        val translateAnim = transition.animateFloat(
            initialValue = 0f,
            targetValue = (spaceMaxWidth * (1 + shimmerWidthPercentage)).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 900,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ), label = "shimmer_anim"
        )

        val brush = Brush.linearGradient(
            colors,
            start = Offset((translateAnim.value - (spaceMaxWidth * shimmerWidthPercentage)).toFloat(),
                spaceMaxHeight
            ),
            end = Offset(translateAnim.value,spaceMaxHeight)
        )

        Spacer(
            modifier = modifier.clip(shape).heightIn(min = 10.dp).background(brush = brush)
        )

    }





}

data class DeferrableJob(val dispather: CoroutineDispatcher, val delay: Long = 0L, val action: ()->Unit)

/**
 * An extension that allows you to start the list of deferred coroutines postponed
 * */
@ExperimentalCoroutinesApi
inline fun  CoroutineScope.startDelayed(deferrableJobs: List<DeferrableJob>, delay: Long = 0){
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
