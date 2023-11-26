package com.learn.worlds.ui.base.add_word

import androidx.compose.runtime.Stable
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow

sealed class AddWordsEvent {
    class OnNativeDataChanged(val nativeData: String) : AddWordsEvent()
    class OnForeignDataChanged(val foreignData: String) : AddWordsEvent()
    object OnSaveLearningItem : AddWordsEvent()
    object OnErrorDismissed : AddWordsEvent()
    object InitCardData : AddWordsEvent()
    object OnPlayAudio : AddWordsEvent()
    object OnStopPlayer : AddWordsEvent()
    object OnPausePlayer : AddWordsEvent()
}

sealed class SpellingCheckState {
    object None : SpellingCheckState()
    object Correct : SpellingCheckState()
    data class Incorrect(val suggestion: String): SpellingCheckState()
}

@Stable
data class AddWordsState(
    val actualSuggestionForeign: MutableStateFlow<SpellingCheckState> = MutableStateFlow(SpellingCheckState.None),
    val nativeText: MutableStateFlow<String> = MutableStateFlow(""),
    val foreignText: MutableStateFlow<String> = MutableStateFlow(""),
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val error: MutableStateFlow<Result.Error?> = MutableStateFlow(null),
    val cardWasAdded: MutableStateFlow<Boolean?> = MutableStateFlow(null),
    val speechFile: MutableStateFlow<String?> = MutableStateFlow(null),
    val imageFile: MutableStateFlow<String?> = MutableStateFlow(null),
    val authState: MutableStateFlow<Boolean?> = MutableStateFlow(null),
    val playerIsPlaying: MutableStateFlow<Boolean?> = MutableStateFlow(null)
) {
    fun isCanToSave(): Boolean {
        return foreignText.value.isNotEmpty() && nativeText.value.isNotEmpty()
    }

    fun isCanToGenerate(): Boolean {
        return isCanToSave() && (speechFile.value.isNullOrEmpty() || imageFile.value.isNullOrEmpty())
    }
}