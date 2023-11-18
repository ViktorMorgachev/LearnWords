package com.learn.worlds.ui.base.show_words

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.utils.Result


sealed class ShowWordsEvent {
    data class OnDeleteItemEvent(val learningItem: LearningItem): ShowWordsEvent()
    data class UpdateCardStatusEvent(val learningItem: LearningItem): ShowWordsEvent()
    data class ShowChangeCardStatusDialog(val learningItem: LearningItem): ShowWordsEvent()
    object DismisErrorDialog: ShowWordsEvent()
    object DismisChangeStatusDialog: ShowWordsEvent()
    object UpdateData: ShowWordsEvent()

}
data class ShowWordsState(
    val learningItems: List<LearningItem> = listOf(),
    val isLoading: Boolean = false,
    val isShowedLoginInfoDialogForUser: Boolean = false,
    val changeStatusDialog: LearningItem? = null,
    val errorDialog: Result.Error? = null,
    val isAuthentificated: Boolean,
    val defaultNativeList: Boolean
)