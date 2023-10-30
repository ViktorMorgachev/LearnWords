package com.learn.worlds.ui.base.show_words

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.utils.Result


data class ShowWordsState(
    val learningItems: List<LearningItem> = listOf(),
    val isLoading: Boolean = false,
    val error: Result.Error? = null,
    val isAuthentificated: Boolean? = null
)