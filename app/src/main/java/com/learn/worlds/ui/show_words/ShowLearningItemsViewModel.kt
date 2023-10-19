package com.learn.worlds.ui.show_words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ShowLearningItemsViewModel @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository,
    private val preferences: MySharedPreferences
) : ViewModel() {


    private val _stateLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())
    val stateLearningItems: StateFlow<List<LearningItem>> = _stateLearningItems
    private val allLearningItems : MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        viewModelScope.launch {
            learningItemsRepository.data.onEach {
                val data = if (it is Result.Success) {
                    it.data
                } else null
                Timber.d("learningItemsState: type ${it.javaClass.simpleName} data $data")

                when (it) {
                    is Result.Loading -> {
                        _loadingState.value = true
                    }

                    is Result.Success -> {
                        if (it.data.size >= preferences.currentLimit){
                            preferences.dataBaseLocked = true
                        }
                        _loadingState.value = false
                        _stateLearningItems.value = it.data
                        allLearningItems.value = it.data
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

    fun dropErrorDialog() {
        _errorState.value = null
    }

  suspend fun filterBy(filter: FilteringType) {
        if (filter == FilteringType.LEARNED) {
            _stateLearningItems.emit(allLearningItems.value.filter { it.learningStatus == LearningStatus.LEARNED.name })
        }
        if (filter == FilteringType.ALL) {
            _stateLearningItems.emit(allLearningItems.value)
        }
    }

    suspend fun sortBy(sortingType: SortingType){
        if (sortingType == SortingType.SORT_BY_NEW) {
            _stateLearningItems.emit(allLearningItems.value.sortedByDescending { it.uid })
        }
        if (sortingType == SortingType.SORT_BY_OLD) {
            _stateLearningItems.emit(allLearningItems.value.sortedBy { it.uid })
        }
    }

    fun isLockedApplication(): Boolean {
        return preferences.currentLimit != Int.MAX_VALUE
    }

    fun dropLimits() {
        preferences.dataBaseLocked = false
        preferences.currentLimit = Int.MAX_VALUE
    }

    suspend fun changeLearningState(newState: String, itemID: Int) =
        learningItemsRepository.changeState(newState, itemID)

}
