package com.learn.worlds.ui

sealed interface TestUIState {
    object Loading : TestUIState
    data class Error(val throwable: Throwable) : TestUIState
    data class Success(val data: List<String>) : TestUIState
}