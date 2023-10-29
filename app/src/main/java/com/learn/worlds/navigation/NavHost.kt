package com.learn.worlds.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.learn.worlds.ui.base.add_word.AddWordsScreen
import com.learn.worlds.ui.base.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.base.subscribe.SubscribeScreen
import com.learn.worlds.ui.login.auth.AuthScreen
import com.learn.worlds.ui.login.sync.SynchronizationScreen

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
                AuthScreen(
                    onSyncAction = { navHostController.navigateSingleTopTo(route = Screen.SynchronizationScreen.route) }, modifier = modifier,)
            }
            composable(route = Screen.SynchronizationScreen.route) {
                SynchronizationScreen(
                    modifier = modifier,
                    onSyncronizedSucces = {
                        navHostController.navigateUp()
                    })
            }
        }
        navigation(startDestination = Screen.LearnScreen.route, route = "MAIN") {
            composable(route = Screen.AddScreen.route) {
                AddWordsScreen(
                    navigateAfterSuccessWasAdded = { navHostController.navigateUp() },
                    modifier = modifier
                )
            }
            composable(route = Screen.LearnScreen.route) {
                ShowLearningWordsScreen(
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