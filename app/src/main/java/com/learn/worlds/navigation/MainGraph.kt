package com.learn.worlds.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.learn.worlds.ui.base.add_word.AddLearningItemsViewModel
import com.learn.worlds.ui.base.add_word.AddWordsScreen
import com.learn.worlds.ui.base.show_words.ShowLearningItemsViewModel
import com.learn.worlds.ui.base.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.base.subscribe.SubscribeScreen

fun NavGraphBuilder.MainGraph(navController: NavController) {
    navigation(startDestination = Screen.LearnScreen.route, route = "LOGIN") {
        composable(route = Screen.AddScreen.route) {
            val viewModel: AddLearningItemsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            AddWordsScreen(
                addWordsState = uiState,
                navigateAfterSuccessWasAdded = { navController.navigateUp() })
        }
        composable(route = Screen.LearnScreen.route) {
            val viewModel: ShowLearningItemsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ShowLearningWordsScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigate = { screen -> navController.navigate(screen.route) }
            )
        }
        composable(route = Screen.SubscribeScreen.route) {
            SubscribeScreen(onByCoffeeAction = {
                navController.popBackStack()
            })
        }
    }
}