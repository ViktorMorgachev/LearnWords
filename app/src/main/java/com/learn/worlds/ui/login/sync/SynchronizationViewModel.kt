package com.learn.worlds.ui.login.sync

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.remote.SynchronizationWorker
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.uniqueSyncronizationUniqueWorkName
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SynchronizationViewModel @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
    private val authService: AuthService,
    private val learningItemsUseCase: LearnItemsUseCase,
) : ViewModel() {
    val uiState = MutableStateFlow(SynchronizationState())

    init {
        Timber.d("viewModel init")
    }

    init {
        viewModelScope.launch {
            learningItemsUseCase.syncItemsFromNetwork().catch {
                if (it == CancellationException()) {
                    uiState.value = uiState.value.copy(
                        cancelledByUser = true
                    )
                }
            }.collect {
                Timber.d("syncronization: $it")
                when (it) {
                    is Result.Success -> {
                        if (it.data.isEmpty()) {
                            emptyItems()
                        } else {
                            itemsLoaded()
                        }
                    }

                    is Result.Complete -> {
                        itemsLoaded()
                    }

                    is Result.Error -> {
                        showError(it)
                    }

                    is Result.Loading -> {}
                }

            }
        }

    }

    private fun showError(result: Result.Error) {
        uiState.value = uiState.value.copy(
            dialogError = result,
            success = false
        )
    }

    private fun itemsLoaded() {
        uiState.value = uiState.value.copy(
            success = true,
            emptyRemoteData = false,
        )
    }

    private fun emptyItems() {
        uiState.value = uiState.value.copy(
            emptyRemoteData = true,
            success = false
        )
    }

    fun handleEvent(synchronizationEvent: SynchronizationEvent) {
        when (synchronizationEvent) {
            is SynchronizationEvent.Cancel -> {
                cancel()
            }

            SynchronizationEvent.DismissDialog -> {
                dismissDialogs()
            }
        }
    }

    private fun dismissDialogs() {
        uiState.value = uiState.value.copy(
            dialogError = null,
            emptyRemoteData = null
        )
    }

    private fun cancel() {
        viewModelScope.cancel()
    }


}