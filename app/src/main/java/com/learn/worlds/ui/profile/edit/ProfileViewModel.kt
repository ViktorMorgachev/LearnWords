package com.learn.worlds.ui.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.ProfileUseCase
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileEditState> = MutableStateFlow(ProfileEditState(loadingState = false))
    val uiState = _uiState.asStateFlow()

    fun handleEvent(event: ProfileEditEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileEditEvent.onChangeFirstNameEvent -> changeFirstName(event.name)
                is ProfileEditEvent.onChangeSecondNameEvent -> changeSecondName(event.name)
                ProfileEditEvent.onSaveProfileEvent -> {
                    saveProfile()
                }
                ProfileEditEvent.onDismissErrorDialogEvent -> dismissSomethingWentWrong()
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(loadingState = true, somethingWentWrongState = false))
            with(_uiState.value) {
                profileUseCase.addProfile(firstName = firstName.trimEnd(), secondName = secondName.trimEnd()).collectLatest {
                    if (it is Result.Error){
                        somethingWentWrong()
                    } else {
                        dismissLoading()
                    }
                }
            }

        }
    }

    private fun changeFirstName(name: String) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(firstName = name))
        }
    }

    private fun somethingWentWrong() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(somethingWentWrongState = true, loadingState = false))
        }
    }

    private fun dismissLoading() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(loadingState = false))
        }
    }

    private fun dismissSomethingWentWrong() {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(somethingWentWrongState = false, loadingState = false))
        }
    }

    private fun changeSecondName(name: String) {
        viewModelScope.launch {
            _uiState.emit(_uiState.value.copy(secondName = name))
        }
    }


}