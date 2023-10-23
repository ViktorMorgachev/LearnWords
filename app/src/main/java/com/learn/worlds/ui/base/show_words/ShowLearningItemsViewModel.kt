package com.learn.worlds.ui.base.show_words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ShowLearningItemsViewModel @Inject constructor(
    private val learnItemsUseCase: LearnItemsUseCase,
    private val preferences: MySharedPreferences
) : ViewModel() {

    val uiState = MutableStateFlow(ShowWordsState())

    private val _stateLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())
    private val allLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())

    init {
        viewModelScope.launch {
            learnItemsUseCase.actualData.onEach {
                val data = if (it is Result.Success) { it.data } else null
                Timber.d("learningItemsState: type ${it.javaClass.simpleName} data $data")
                when (it) {
                    is Result.Loading -> {
                        uiState.value = uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        if (!preferences.subscribedByUser) {
                            if (it.data.size >= preferences.defaultLimit) {
                                preferences.dataBaseLocked = true
                            }
                        }
                        uiState.value = uiState.value.copy(
                            isLoading =  false,
                            error = null,
                            learningItems =  getSortedAndFilteringData(it.data)
                        )
                        allLearningItems.value = it.data
                    }

                    is Result.Error -> {
                        uiState.value = uiState.value.copy(
                            isLoading =  false,
                            error = it.error
                        )
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
        uiState.value = uiState.value.copy(
            error = null
        )
    }

    private fun getSortedAndFilteringData(allLearningItems: List<LearningItem>): List<LearningItem> {
        var actualList = mutableListOf<LearningItem>().apply {
            addAll(allLearningItems)
        }
        var filtering: ((LearningItem) -> Boolean)? = null
        preferences.savedFilteringType?.let { prefsFiltering ->
            if (prefsFiltering == FilteringType.LEARNED.name) {
                filtering = { it.learningStatus == LearningStatus.LEARNED.name }
            }
        }
        filtering?.let {
            actualList = mutableListOf<LearningItem>().apply {
                actualList.filter(it)
            }
        }
        preferences.savedSortingType?.let {
            return if (it == SortingType.SORT_BY_NEW.name) {
                actualList.sortedByDescending { it.timeStampUIID }
            } else actualList.sortedBy { it.timeStampUIID }
        }
        return actualList
    }

    suspend fun filterBy(filter: FilteringType) {
        preferences.savedFilteringType = filter.name
        Timber.d("filterBy: ${filter.name}")
        if (filter == FilteringType.LEARNED) {
            _stateLearningItems.emit(allLearningItems.value.filter { it.learningStatus == LearningStatus.LEARNED.name })
        }
        if (filter == FilteringType.ALL) {
            _stateLearningItems.emit(allLearningItems.value)
        }
    }

    suspend fun sortBy(sortingType: SortingType) {
        preferences.savedSortingType = sortingType.name
        Timber.d("sortBy: ${sortingType.name}")
        if (sortingType == SortingType.SORT_BY_NEW) {
            Timber.d("sorted: ${allLearningItems.value.sortedByDescending { it.timeStampUIID }.joinToString(", ")}")
            _stateLearningItems.emit(allLearningItems.value.sortedByDescending { it.timeStampUIID })
        }
        if (sortingType == SortingType.SORT_BY_OLD) {
            Timber.d("sorted: ${allLearningItems.value.sortedBy { it.timeStampUIID }.joinToString(", ")}")
            _stateLearningItems.emit(allLearningItems.value.sortedBy { it.timeStampUIID })
        }
    }

    fun isLockedApplication(): Boolean {
        return preferences.dataBaseLocked
    }

    fun dropLimits() {
        preferences.subscribedByUser = true
        preferences.dataBaseLocked = false
    }

}
