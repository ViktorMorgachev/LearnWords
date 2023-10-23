package com.learn.worlds.ui.base.show_words

import com.learn.worlds.data.model.base.LearningItem


data class ShowWordsState(
    val learningItems: List<LearningItem> = listOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthentificated: Boolean? = null
)