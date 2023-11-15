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
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ShowLearningItemsViewModel @Inject constructor(
    private val learnItemsUseCase: LearnItemsUseCase,
    private val preferences: MySharedPreferences,
    private val uiPreferences: UISharedPreferences,
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {

    private val _uiState: MutableStateFlow<ShowWordsState> = MutableStateFlow(ShowWordsState())
    val uiState = _uiState.asStateFlow()
    private val allLearningItems: MutableStateFlow<List<LearningItem>> = MutableStateFlow(listOf())


    init {
        checkForAuthenticated()
        updateData()
    }

    private fun updateData() {
        viewModelScope.launch {
            learnItemsUseCase.actualData().flowOn(Dispatchers.IO).collect { data ->
                Timber.d("actualData: ${data.joinToString(",\n")}")
                allLearningItems.emit(data)
                _uiState.emit(uiState.value.copy(
                    composeNeedUpdate = true,
                    isLoading = false,
                    errorDialog = null,
                    learningItems = getSortedAndFilteringData(data)
                ))
            }
        }
    }

    fun checkForAuthenticated() {
        viewModelScope.launch {
            firebaseAuthService.authState.collectLatest {
                _uiState.value = uiState.value.copy(
                    isAuthentificated = it
                )
            }
        }
    }

   private fun showErrorDialog(error: Result.Error) {
        viewModelScope.launch {
            firebaseAuthService.authState.collectLatest {
                _uiState.value = uiState.value.copy(
                    errorDialog = error
                )
            }
        }
    }


    fun handleEvent(showWordsEvent: ShowWordsEvent) {
        when (showWordsEvent) {
            is ShowWordsEvent.UpdateCardStatusEvent -> {
                dropChangeStatusDialog()
                viewModelScope.launch {
                    learnItemsUseCase.changeItemsStatus(learningItem = showWordsEvent.learningItem).flowOn(Dispatchers.IO).collectLatest { result->
                        if (result is Result.Error){
                            showErrorDialog(result)
                            return@collectLatest
                        }
                        if (result is Result.Complete){
                            updateData()
                        }
                    }
                }
            }

            is ShowWordsEvent.DeleteItemEvent -> {
                viewModelScope.launch {
                    learnItemsUseCase.deleteWordItem(learningItem = showWordsEvent.learningItem)
                        .collectLatest { result ->
                            if (result is Result.Error){
                                showErrorDialog(result)
                                return@collectLatest
                            }
                            if (result is Result.Complete){
                                updateData()
                            }
                            Timber.d("DeleteItemEvent: $showWordsEvent result: $result")
                        }
                }
            }

            is ShowWordsEvent.ShowChangeCardStatusDialog -> showChangeStatusDialog(showWordsEvent.learningItem)
            ShowWordsEvent.DismisErrorDialog -> dropErrorDialog()
            ShowWordsEvent.DismisChangeStatusDialog -> dropChangeStatusDialog()
            ShowWordsEvent.ListWasUpdated -> listWasUpdated()
        }
    }

    private fun listWasUpdated(){
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    composeNeedUpdate = false
                )
            )
        }
    }

    fun isShowedLoginInfoDialogForUser(): Boolean {
        return uiPreferences.isShowedLoginInfo
    }


    private fun dropChangeStatusDialog() {
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    changeStatusDialog = null
                )
            )
        }
    }

    private fun showChangeStatusDialog(learningItem: LearningItem) {
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    changeStatusDialog = learningItem
                )
            )
        }
    }

    private fun dropErrorDialog() {
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    errorDialog = null
                )
            )
        }
    }

    fun saveShowedLoginInfoDialog() {
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
        Timber.d("filterBy: ${filter.name}")
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    composeNeedUpdate = true,
                    learningItems = getSortedAndFilteringData(allLearningItems.value)
                )
            )
        }
    }

    suspend fun sortBy(sortingType: SortingType) {
        preferences.savedSortingType = sortingType.name
        val sortedData = getSortedAndFilteringData(allLearningItems.value)
        Timber.d("sortBy: ${sortingType.name} result: ${sortedData.joinToString(", \n")}")
        viewModelScope.launch {
            _uiState.emit(
                uiState.value.copy(
                    composeNeedUpdate = true,
                    learningItems = sortedData
                )
            )
        }
    }

}
