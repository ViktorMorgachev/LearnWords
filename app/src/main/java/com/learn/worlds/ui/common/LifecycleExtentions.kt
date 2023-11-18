package com.learn.worlds.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

@Composable
fun DoOnLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current, lifecycleState: Lifecycle.Event, action: ()->Unit) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleState) {
                action.invoke()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}