package com.learn.worlds.ui.login.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
) : ViewModel() {
    val uiState = MutableStateFlow(AuthenticationState())

    private fun toggleAuthenticationMode() {
        val authenticationMode = uiState.value.authenticationMode
        val newAuthenticationMode = if (
            authenticationMode == AuthenticationMode.SIGN_IN
        ) {
            AuthenticationMode.SIGN_UP
        } else {
            AuthenticationMode.SIGN_IN
        }
        uiState.value = uiState.value.copy(
            authenticationMode = newAuthenticationMode
        )
    }

    fun handleEvent(authenticationEvent: AuthenticationEvent) {
        when (authenticationEvent) {
            AuthenticationEvent.ToggleAuthenticationMode -> toggleAuthenticationMode()
            is AuthenticationEvent.EmailChanged -> updateEmail(authenticationEvent.emailAddress)
            is AuthenticationEvent.PasswordChanged -> updatePassword(authenticationEvent.password)
            is AuthenticationEvent.Authenticate -> authenticate()
            is AuthenticationEvent.DialogDismiss -> dismissDialogs()
        }
    }

    private fun dismissDialogs() {
        uiState.value = uiState.value.copy(
            dialogError = null,
            dialogAuthSuccess = null
        )
    }


    private fun authenticate() {
        uiState.value = uiState.value.copy(
            isLoading = true
        )
        uiState.value.let {
            if (it.authenticationMode == AuthenticationMode.SIGN_UP) {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = firebaseAuthService.signUp(password = it.password!!, email = it.email!!)
                    if (result is Result.Complete) {
                        signUpAction()
                        delay(1000)
                        dismissDialogs()
                        syncronizeData()
                    }
                    if (result is Result.Error) {
                        authError(result)
                    }

                }
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = firebaseAuthService.signIn(password = it.password!!, email = it.email!!)
                    if (result is Result.Complete) {
                        signInAction()
                        delay(1000)
                        dismissDialogs()
                        syncronizeData()
                    }
                    if (result is Result.Error) {
                        authError(result)
                    }
                }

            }
        }

    }

    private fun syncronizeData() {
        uiState.value = uiState.value.copy(
            isSynchronization = true,
        )
    }


    private fun updateEmail(email: String) {
        uiState.value = uiState.value.copy(
            email = email
        )
    }


    private fun signInAction() {
        uiState.value = uiState.value.copy(
            isLoading = false,
            dialogAuthSuccess = AuthSuccessEvent.SIGN_UP
        )
    }

    private fun signUpAction() {
        uiState.value = uiState.value.copy(
            isLoading = false,
            dialogAuthSuccess = AuthSuccessEvent.SIGN_IN
        )
    }

    private fun authError(error: Result.Error) {
        uiState.value = uiState.value.copy(
            isLoading = false,
            dialogError = error
        )
    }

    private fun updatePassword(password: String) {
        uiState.value = uiState.value.copy(
            password = password
        )
        val requirements = mutableListOf<PasswordRequirement>()
        if (password.length > 7) {
            requirements.add(PasswordRequirement.EIGHT_CHARACTERS)
        }
        if (password.any { it.isUpperCase() }) {
            requirements.add(PasswordRequirement.CAPITAL_LETTER)
        }
        if (password.any { it.isDigit() }) {
            requirements.add(PasswordRequirement.NUMBER)
        }

        uiState.value = uiState.value.copy(
            password = password,
            passwordRequirements = requirements.toList()
        )
    }

}