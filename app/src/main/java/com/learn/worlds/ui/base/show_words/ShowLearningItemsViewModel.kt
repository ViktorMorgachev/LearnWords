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
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun handleEvent(showWordsEvent: ShowWordsEvent) {
        when (showWordsEvent) {
            is ShowWordsEvent.UpdateCardStatusEvent -> {
                dropChangeStatusDialog()
                viewModelScope.launch {
                    learnItemsUseCase.changeItemsStatus(learningItem = showWordsEvent.learningItem).collect { result->
                        Timber.d("UpdateCardStatusEvent: $showWordsEvent result: $result")
                    }
                }
            }
            is ShowWordsEvent.DeleteItemEvent -> {
                viewModelScope.launch {
                    learnItemsUseCase.deleteWordItem(learningItem = showWordsEvent.learningItem).collect { result->
                        Timber.d("DeleteItemEvent: $showWordsEvent result: $result")
                    }
                }
            }

            is ShowWordsEvent.ShowChangeCardStatusDialog -> showChangeStatusDialog(showWordsEvent.learningItem)
            ShowWordsEvent.DismisErrorDialog -> dropErrorDialog()
            ShowWordsEvent.DismisChangeStatusDialog -> dropChangeStatusDialog()
        }
    }

    fun isShowedLoginInfoDialogForUser(): Boolean{
        return  uiPreferences.isShowedLoginInfo
    }

    init {
        viewModelScope.launch {
            learnItemsUseCase.actualData().collect { data->
                Timber.d("actualData: ${data.joinToString(",\n")}")
                allLearningItems.emit(data)
                uiState.value = uiState.value.copy(
                    isLoading =  false,
                    errorDialog = null,
                    learningItems = getSortedAndFilteringData(data)
                )
            }
        }

    }

    private fun dropChangeStatusDialog() {
        uiState.value = uiState.value.copy(
            changeStatusDialog = null
        )
    }

    private fun showChangeStatusDialog(learningItem: LearningItem) {
        uiState.value = uiState.value.copy(
           changeStatusDialog = learningItem
        )
    }

   private fun dropErrorDialog() {
        uiState.value = uiState.value.copy(
            errorDialog = null
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
        Timber.d("filterBy: ${filter.name}")
    }

    suspend fun sortBy(sortingType: SortingType) {
        preferences.savedSortingType = sortingType.name
        val sortedData = getSortedAndFilteringData(allLearningItems.value)
        Timber.d("sortBy: ${sortingType.name} result: ${sortedData.joinToString(", \n")}")
        uiState.value = uiState.value.copy(
            learningItems =  sortedData
        )
    }

}
