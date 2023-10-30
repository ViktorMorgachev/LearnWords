package com.learn.worlds.ui.base.show_words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.prefs.UISharedPreferences
import com.learn.worlds.servises.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ShowLearningItemsViewModel @Inject constructor(
    private val learnItemsUseCase: LearnItemsUseCase,
    private val preferences: MySharedPreferences,
    private val uiPreferences: UISharedPreferences,
    private val authService: AuthService
) : ViewModel() {

    val uiState = MutableStateFlow(ShowWordsState())

    val actualItems : MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())

    private val allLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())

    init {
        checkForAuthenticated()
    }

    fun checkForAuthenticated() {
        viewModelScope.launch {
            authService.authState.collectLatest {
                uiState.value = uiState.value.copy(
                    isAuthentificated = it
                )
            }
        }
    }

    fun isShowedLoginInfoDialogForUser(): Boolean{
        return  uiPreferences.isShowedLoginInfo
    }

    init {
        viewModelScope.launch {
            learnItemsUseCase.actualData().collect { data->
                allLearningItems.value = data
                Timber.d("actualData: ${data.joinToString(",\n")}")
                uiState.value = uiState.value.copy(
                    isLoading =  false,
                    error = null,
                    learningItems = getSortedAndFilteringData(data)
                )
                actualItems.emit(getSortedAndFilteringData(data))
                delay(2000)
                sortBy(SortingType.SORT_BY_NEW)
                delay(2000)
                sortBy(SortingType.SORT_BY_OLD)

            }
        }

    }

    fun dropErrorDialog() {
        uiState.value = uiState.value.copy(
            error = null
        )
    }

    fun saveShowedLoginInfoDialog(){
        uiPreferences.isShowedLoginInfo = true
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
        uiState.value = uiState.value.copy(
            learningItems = getSortedAndFilteringData(allLearningItems.value)
        )
        actualItems.emit(getSortedAndFilteringData(allLearningItems.value))
        Timber.d("filterBy: ${filter.name}")
    }

    suspend fun sortBy(sortingType: SortingType) {
        preferences.savedSortingType = sortingType.name
        val sortedData = getSortedAndFilteringData(allLearningItems.value)
        Timber.d("sortBy: ${sortingType.name} result: ${sortedData.joinToString(", \n")}")
        uiState.value = uiState.value.copy(
            learningItems =  sortedData
        )
        actualItems.emit(sortedData)
    }

}
