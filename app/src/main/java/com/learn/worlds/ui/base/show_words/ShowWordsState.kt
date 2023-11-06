package com.learn.worlds.ui.base.show_words

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.utils.Result


sealed class ShowWordsEvent {
    data class DeleteItemEvent(val learningItem: LearningItem): ShowWordsEvent()
    data class UpdateCardStatusEvent(val learningItem: LearningItem): ShowWordsEvent()
    object ShowChangeCardStatusDialog: ShowWordsEvent()
    object DismisErrorDialog: ShowWordsEvent()
    object DismisChangeStatusDialog: ShowWordsEvent()
}
data class ShowWordsState(
    val learningItems: List<LearningItem> = listOf(),
    val isLoading: Boolean = false,
    val changeStatusDialog: Boolean = false,
    val errorDialog: Result.Error? = null,
    val isAuthentificated: Boolean? = null
)