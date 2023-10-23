package com.learn.worlds.navigation


import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.learn.worlds.ui.auth.AuthScreen
import com.learn.worlds.ui.auth.AuthViewModel

fun NavGraphBuilder.AuthGraph(navController: NavController) {
    navigation(startDestination = Screen.AuthScreen.route, route = "AUTH") {
        composable(route = Screen.AuthScreen.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by  viewModel.uiState.collectAsStateWithLifecycle()
            AuthScreen(authenticationState = uiState, viewModel = viewModel, onAuthSuccessAction = {
                navController.navigateUp()
            })
        }
    }
}