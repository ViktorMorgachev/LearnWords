package com.learn.worlds.ui.base.add_word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel  @Inject constructor(
    private val learnItemsUseCase: LearnItemsUseCase
) : ViewModel() {

    val uiState = MutableStateFlow(AddWordsState())
    val stateWasSavedSuccessfully = MutableStateFlow(false)
   private fun addLearningItem(learningItem: LearningItem){
       viewModelScope.launch {
           learnItemsUseCase.addLearningItem(learningItem).collect {
               when(it){
                   Result.Complete -> stateWasSavedSuccessfully.value = true
                   is Result.Error -> showError(it)
                   Result.Loading -> showLoading()
                   is Result.Success -> stateWasSavedSuccessfully.value = true
               }
           }
       }
    }

    fun handleEvent(addWordsEvent: AddWordsEvent) {
        when (addWordsEvent) {
            AddWordsEvent.ErrorDismissed -> { dismissError()}
            is AddWordsEvent.ForeignDataChanged -> { saveForeignData(addWordsEvent.foreignData.trimEnd()) }
            is AddWordsEvent.NativeDataChanged ->{saveNativeData(addWordsEvent.nativeData.trimEnd())}
            is AddWordsEvent.SaveLearningItem -> {saveData()}
        }
    }

    private fun saveData(){
        uiState.value.let { state->
            if (state.isDataValid()){
                addLearningItem(LearningItem(nativeData = state.nativeData!!, foreignData = state.foreignData!!))
            } else {
                showError()
            }
        }
    }

    private fun saveForeignData(foreignData: String) {
        uiState.value = uiState.value.copy(
            foreignData = foreignData
        )
    }

    private fun showError(error: Result.Error? = null) {
        uiState.value = uiState.value.copy(
            error = error,
            isLoading = false
        )
    }

    private fun showLoading() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )
    }

    private fun saveNativeData(nativeData: String) {
        uiState.value = uiState.value.copy(
            nativeData = nativeData
        )
    }

    private fun dismissError() {
        uiState.value = uiState.value.copy(
            error = null
        )
    }

}


