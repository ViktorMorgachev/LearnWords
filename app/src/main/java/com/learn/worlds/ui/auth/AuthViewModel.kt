package com.learn.worlds.ui.auth

import androidx.lifecycle.ViewModel
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.servises.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
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
            is AuthenticationEvent.ErrorDismissed -> dismissError()
        }
    }

    private fun dismissError() {
        uiState.value = uiState.value.copy(
            error = null
        )
    }

    private fun authenticate() {
        uiState.value = uiState.value.copy(
            isLoading = true
        )
        uiState.value.let {
            if (it.authenticationMode == AuthenticationMode.SIGN_UP){
                authService.signUp(password = it.password!!, email = it.email!!)
            } else {
                authService.signIn(password = it.password!!, email = it.email!!)
            }
        }

    }

    private fun updateEmail(email: String) {
        uiState.value = uiState.value.copy(
            email = email
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