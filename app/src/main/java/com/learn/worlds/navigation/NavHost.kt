package com.learn.worlds.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.learn.worlds.ui.base.add_word.AddLearningItemsViewModel
import com.learn.worlds.ui.base.add_word.AddWordsScreen
import com.learn.worlds.ui.base.show_words.ShowLearningItemsViewModel
import com.learn.worlds.ui.base.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.base.subscribe.SubscribeScreen
import com.learn.worlds.ui.login.auth.AuthScreen
import com.learn.worlds.ui.login.auth.AuthViewModel
import com.learn.worlds.ui.login.sync.SynchronizationEvent
import com.learn.worlds.ui.login.sync.SynchronizationScreen
import com.learn.worlds.ui.login.sync.SynchronizationViewModel
import kotlinx.coroutines.delay

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

@Composable
fun MyNavHost(
    navHostController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = "MAIN",
        route = "ROOT"
    ) {
        navigation(startDestination = Screen.AuthScreen.route, route = "LOGIN") {
            composable(route = Screen.AuthScreen.route) {
                val viewModel: AuthViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                if (uiState.isSynchronization == true) {
                    navHostController.navigateSingleTopTo(route = Screen.SynchronizationScreen.route)
                }
                AuthScreen(authenticationState = uiState,
                    viewModel = viewModel,
                    onAuthSuccessAction = {}, modifier = modifier
                )
            }
            composable(route = Screen.SynchronizationScreen.route) {
                val viewModel: SynchronizationViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SynchronizationScreen(
                    modifier = modifier,
                    handleEvent = { viewModel.handleEvent(it) },
                    synchronizationState = uiState,
                    onSyncronizedSucces = {
                        navHostController.navigateUp()
                    })
            }
        }
        navigation(startDestination = Screen.LearnScreen.route, route = "MAIN") {
            composable(route = Screen.AddScreen.route) {
                val viewModel: AddLearningItemsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                AddWordsScreen(
                    addWordsState = uiState,
                    navigateAfterSuccessWasAdded = { navHostController.navigateUp() },
                    modifier = modifier
                )
            }
            composable(route = Screen.LearnScreen.route) {
                val viewModel: ShowLearningItemsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ShowLearningWordsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigate = { screen -> navHostController.navigate(screen.route) },
                    modifier = modifier
                )
            }
            composable(route = Screen.SubscribeScreen.route) {
                SubscribeScreen(
                    onByCoffeeAction = { navHostController.popBackStack() },
                    modifier = modifier
                )
            }
        }
    }


}