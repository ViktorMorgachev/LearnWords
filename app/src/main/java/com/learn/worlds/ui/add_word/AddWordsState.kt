package com.learn.worlds.ui.add_word

import com.learn.worlds.utils.Result

sealed class AddWordsEvent {
    class NativeDataChanged(val nativeData: String): AddWordsEvent()
    class ForeignDataChanged(val foreignData: String): AddWordsEvent()
    object SaveLearningItem: AddWordsEvent()
    object ErrorDismissed: AddWordsEvent()
}
data class AddWordsState(
    val nativeData: String? = null,
    val foreignData: String? = null,
    val isLoading: Boolean = false,
    val error: Result.Error? = null
){
    fun isDataValid(): Boolean {
        return nativeData != null && foreignData != null
    }
}