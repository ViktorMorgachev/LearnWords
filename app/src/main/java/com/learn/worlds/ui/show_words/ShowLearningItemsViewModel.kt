package com.learn.worlds.ui.show_words

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
import kotlinx.coroutines.flow.StateFlow
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


    private val _stateLearningItems: MutableStateFlow<List<LearningItem>> =
        MutableStateFlow(listOf())
    val stateLearningItems: StateFlow<List<LearningItem>> = _stateLearningItems
    private val allLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())

    private val _loadingState = MutableStateFlow(true)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        viewModelScope.launch {
            learnItemsUseCase.actualData.onEach {
                val data = if (it is Result.Success) { it.data } else null
                Timber.d("learningItemsState: type ${it.javaClass.simpleName} data $data")
                when (it) {
                    is Result.Loading -> {
                        _loadingState.value = true
                    }

                    is Result.Success -> {
                        if (!preferences.subscribedByUser) {
                            if (it.data.size >= preferences.defaultLimit) {
                                preferences.dataBaseLocked = true
                            }
                        }
                        _loadingState.value = false
                        allLearningItems.value = it.data
                        _stateLearningItems.value = getSortedAndFilteringData(it.data)
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
                actualList.sortedByDescending { it.uid }
            } else actualList.sortedBy { it.uid }
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
            Timber.d(
                "sorted: ${
                    allLearningItems.value.sortedByDescending { it.uid }.joinToString(", ")
                }"
            )
            _stateLearningItems.emit(allLearningItems.value.sortedByDescending { it.uid })
        }
        if (sortingType == SortingType.SORT_BY_OLD) {
            Timber.d("sorted: ${allLearningItems.value.sortedBy { it.uid }.joinToString(", ")}")
            _stateLearningItems.emit(allLearningItems.value.sortedBy { it.uid })
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
