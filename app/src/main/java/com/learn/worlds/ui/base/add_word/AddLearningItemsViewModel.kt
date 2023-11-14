package com.learn.worlds.ui.base.add_word

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.remote.CommonLanguage
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.AudioPlayer
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.emitIf
import com.learn.worlds.utils.getMp3File
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val learnItemsUseCase: LearnItemsUseCase,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    var uiState = AddWordsState(
            nativeText = MutableStateFlow("вселенная"),
            foreignText = MutableStateFlow("unverse")
        )

    init {
        viewModelScope.launch {
            audioPlayer.isPlaying.collectLatest {
                updatePlayingState(it)
            }
        }
    }

    private fun addLearningItem(learningItem: LearningItem) {
        viewModelScope.launch {
            learnItemsUseCase.addLearningItem(learningItem).collect {
                when (it) {
                    Result.Complete -> cardWasAdded()
                    is Result.Error -> showError(it)
                    Result.Loading -> showLoading()
                    is Result.Success -> cardWasAdded()
                }
            }
        }
    }

    fun handleEvent(addWordsEvent: AddWordsEvent) {
        when (addWordsEvent) {
            AddWordsEvent.OnErrorDismissed -> dismissError()
            is AddWordsEvent.OnForeignDataChanged -> saveForeign(addWordsEvent.foreignData)
            is AddWordsEvent.OnNativeDataChanged -> saveNative(addWordsEvent.nativeData)
            is AddWordsEvent.OnSaveLearningItem -> saveData()
            AddWordsEvent.InitCardData ->{
                if (uiState.actualSuggestionForeign.value != SpellingCheckState.None){
                    initCardData()
                } else {
                    spellCheckForeign()
                }

            }
            AddWordsEvent.OnPlayAudio -> playAudio()
            AddWordsEvent.OnStopPlayer -> stopAudio()
            AddWordsEvent.OnPausePlayer -> audioPlayer.pause()
        }
    }

    private fun playAudio() {
        uiState.speechFile.value?.let { fileName ->
            getMp3File(context = context, name = fileName)?.let {
                audioPlayer.play(it)
            }
        }
    }

    private fun stopAudio() {
        audioPlayer.stopSound()
        uiState = AddWordsState()

    }

    private fun updatePlayingState(state: Boolean) {
        viewModelScope.launch {
            uiState.playerIsPlaying.emit(state)
        }

    }
    private fun spellCheckForeign() {
        viewModelScope.launch {
            learnItemsUseCase.spellCheck(spellTextCheck = SpellTextCheck(requestText = uiState.foreignText.value))
                .catch {
                    t->Timber.e(t)
                    showError(Result.Error(errorType = ErrorType.FAILED_TO_CHECK_SPELL_TEXT))
                }
                .collectLatest {
                    if (it is Result.Success){
                        showForeignSuggestion(it.data.suggestion!!)
                    }
                    if (it is Result.Error){
                        showError(it)
                    }
            }
        }
    }

    private fun initCardData() {
        showLoading()
        viewModelScope.launch {
            learnItemsUseCase.getImage(text = uiState.foreignText.value.trimEnd())
                .catch {
                Timber.e(it)
                showError(Result.Error())
            }.collectLatest {
                when(it){
                    Result.Complete -> {}
                    is Result.Error -> showError(it)
                    Result.Loading -> {}
                    is Result.Success -> {
                        hideLoading()
                        setImage(it.data)
                    }
                }
            }
        }
        viewModelScope.launch {
            learnItemsUseCase.getTextSpeech(text = uiState.foreignText.value.trimEnd(), language = CommonLanguage.English)
                .catch {
                Timber.e(it)
                showError(Result.Error())
            }.collectLatest {
                when(it){
                    Result.Complete -> {}
                    is Result.Error -> showError(it)
                    Result.Loading -> {}
                    is Result.Success -> {
                        hideLoading()
                        setTextSpeech(it.data)
                    }
                }
            }
        }
    }

    private fun cardWasAdded() {
        viewModelScope.launch {
            uiState.cardWasAdded.emit(true)
        }
    }

    private fun saveData() {
        if (uiState.isCanToSave()) {
            addLearningItem(
                LearningItem(
                    nativeData = uiState.nativeText.value.trimEnd(),
                    foreignData = uiState.foreignText.value.trimEnd()
                )
            )
        } else {
            showError()
        }
    }


    private fun isNeedShowErrorDialod(): Boolean {
        return uiState.imageFile.value == null && uiState.speechFile.value == null
    }


    private fun setTextSpeech(fileName: String?) {
        viewModelScope.launch {
            uiState.speechFile.emit(fileName)
        }
    }

    private fun setImage(fileName: String?) {
        viewModelScope.launch {
            uiState.imageFile.emit(fileName)
        }
    }

    private fun saveForeign(foreignData: String) {
        viewModelScope.launch {
            uiState.foreignText.emit(foreignData)
        }
    }

    private fun saveNative(nativeData: String) {
        viewModelScope.launch {
            uiState.nativeText.emit(nativeData)
        }
    }

    private fun showError(error: Result.Error? = null) {
        if (isNeedShowErrorDialod()){
            viewModelScope.launch {
                uiState.isLoading.emit(false)
                uiState.error.emit(error)
            }
        }
    }

    private fun showForeignSuggestion(suggestion: String) {
        viewModelScope.launch {
            if (suggestion.isNotEmpty()){
                uiState.actualSuggestionForeign.emit(SpellingCheckState.Incorrect(suggestion = suggestion))

            } else {
                uiState.actualSuggestionForeign.emit(SpellingCheckState.Correct)
            }

        }
    }

    private fun hideLoading() {
        viewModelScope.launch {
            uiState.isLoading.emit(false)
            uiState.error.emit(null)
        }
    }

    private fun showLoading() {
        viewModelScope.launch {
            uiState.isLoading.emit(true)
        }
    }

    private fun dismissError() {
        viewModelScope.launch {
            uiState.error.emit(null)
        }
    }

}


