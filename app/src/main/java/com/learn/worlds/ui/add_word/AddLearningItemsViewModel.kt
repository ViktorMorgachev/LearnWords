package com.learn.worlds.ui.add_word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.ui.LearningItemsUIState
import com.learn.worlds.ui.LearningItemsUIState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel  @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository
) : ViewModel() {

    val uiState: StateFlow<LearningItemsUIState> = learningItemsRepository.data.map(::Success).catch {
        LearningItemsUIState.Error(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(3000), LearningItemsUIState.Loading)


     fun addLearningItem(learningItem: LearningItem){
         viewModelScope.launch {
             learningItemsRepository.addLearningItem(learningItem)
         }
    }
}


