package com.learn.worlds.ui.login.sync

import com.learn.worlds.utils.Result

sealed class SynchronizationEvent{
    object Cancel: SynchronizationEvent()
    object DismissDialog: SynchronizationEvent()
}
data class SynchronizationState(
    val dialogError: Result.Error? = null,
    val emptyRemoteData: Boolean? = null,
    val success: Boolean? = null,
    val cancelledByUser: Boolean? = null
)
