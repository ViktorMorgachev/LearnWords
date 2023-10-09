package com.learn.worlds.ui

import com.learn.worlds.data.model.base.LearningItem

sealed interface LearningItemsUIState {
    object Loading : LearningItemsUIState
    data class Error(val throwable: Throwable) : LearningItemsUIState
    data class Success(val data: List<LearningItem>) : LearningItemsUIState
}