package com.learn.worlds.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learn.worlds.ui.add_word.AddLearningItemsViewModel
import com.learn.worlds.ui.add_word.AddWordsScreen
import com.learn.worlds.ui.auth.AuthScreen
import com.learn.worlds.ui.auth.AuthViewModel
import com.learn.worlds.ui.show_words.ShowLearningItemsViewModel
import com.learn.worlds.ui.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.subscribe.SubscribeScreen

@Composable
fun MyNavHost(
    navHostController: NavHostController,
    startDestination: String,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(route = Screen.AddScreen.route) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val viewModel: AddLearningItemsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AddWordsScreen(
                    addWordsState = uiState,
                    navigateAfterSuccessWasAdded = { navHostController.navigateUp() })
            }
        }
        composable(route = Screen.LearnScreen.route) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val viewModel: ShowLearningItemsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ShowLearningWordsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigate = { screen -> navHostController.navigate(screen.route) }
                )
            }
        }
        composable(route = Screen.SubscribeScreen.route) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SubscribeScreen(onByCoffeeAction = {
                    navHostController.popBackStack()
                })
            }
        }
        composable(route = Screen.AuthScreen.route) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val viewModel: AuthViewModel = hiltViewModel()
                val uiState by  viewModel.uiState.collectAsStateWithLifecycle()
                AuthScreen(authenticationState = uiState, viewModel = viewModel, onAuthSuccessAction = {
                    navHostController.popBackStack()
                })
            }
        }
    }

}