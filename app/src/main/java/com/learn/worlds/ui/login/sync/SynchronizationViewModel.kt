package com.learn.worlds.ui.login.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.servises.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SynchronizationViewModel @Inject constructor(
    private val authService: AuthService,
    private val learningItemsUseCase: LearnItemsUseCase,
) : ViewModel() {
    val uiState = MutableStateFlow(SynchronizationState())

    init {
        loadItemsFromFirebase()
    }

    private fun loadItemsFromFirebase() {
        // TODO: Нужно учесть что в БД может быть изначально пустая это нужно пробросить пользователю 
        viewModelScope.launch {
            learningItemsUseCase.loadItemsFromNetwork()
        }
    }

    fun handleEvent(synchronizationEvent: SynchronizationEvent) {
        when (synchronizationEvent) {
            is SynchronizationEvent.Cancel ->{ cancel()}
            SynchronizationEvent.DismissDialog -> {dismissDialogs()}
        }
    }

    private fun dismissDialogs() {
        uiState.value = uiState.value.copy(
            dialogError = null
        )
    }

    private fun cancel(){

    }


}