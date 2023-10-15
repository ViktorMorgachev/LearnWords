package com.learn.worlds.ui.show_words

import android.view.KeyCharacterMap.UnavailableException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.ui.TestUIState
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ShowLearningItemsViewModel @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository,
    private val mySharedPreferences: MySharedPreferences
) : ViewModel() {

    private val _stateLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())
    val stateLearningItems: StateFlow<List<LearningItem>> = _stateLearningItems

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        viewModelScope.launch {
            learningItemsRepository.data.onEach {
                val data = if (it is Result.Success){ it.data } else null
                Timber.d("learningItemsState: type ${it.javaClass.simpleName} data $data")
                when(it) {
                    is Result.Loading -> {
                        _loadingState.value = true
                    }
                    is Result.Success -> {
                        _loadingState.value = false
                        mySharedPreferences.canAddNewLearnItem = it.data.size < mySharedPreferences.currentLimit
                        _stateLearningItems.value = it.data
                    }
                    is Result.Error -> {
                        _loadingState.value = false
                        _errorState.value = it.error
                    }
                    Result.Complete -> {}
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                Result.Loading
            ).collect()
        }
    }

    fun dropErrorDialog(){
        _errorState.value = null
    }

    fun dropLimits(){
        mySharedPreferences.currentLimit = Int.MAX_VALUE
    }

    suspend fun changeLearningState(newState: String, itemID: Int) = learningItemsRepository.changeState(newState, itemID)

}
