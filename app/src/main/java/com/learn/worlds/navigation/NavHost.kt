package com.learn.worlds.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.learn.worlds.ui.base.add_word.AddWordsScreen
import com.learn.worlds.ui.base.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.login.auth.AuthScreen
import com.learn.worlds.ui.login.sync.SynchronizationScreen
import com.learn.worlds.ui.preferences.PreferencesScreenBase
import com.learn.worlds.ui.profile.ProfileScreenBase
import com.learn.worlds.ui.profile.ProfileScreenUI
import com.learn.worlds.ui.profile.edit.ProfileEditScreen

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

// todo refactoring (extract to other files)
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
        navigation(startDestination = Screen.WordsListScreen.route, route = "DRAWER") {
            composable(route = Screen.ProfileScreen.route) {
                ProfileScreenUI(
                    modifier = modifier,
                    navigateToFillProfile = {
                        navHostController.navigate(Screen.ProfileScreenEditing.route)
                    },
                    navigateToBack = {
                        navHostController.popBackStack()
                    }
                )
            }
            composable(route = Screen.PreferencesScreen.route) {
                PreferencesScreenBase(modifier = modifier)
            }
            composable(route = Screen.ProfileScreenEditing.route) {
                ProfileEditScreen(modifier = modifier)
            }
        }
        navigation(startDestination = Screen.AuthScreen.route, route = "LOGIN") {
            composable(route = Screen.AuthScreen.route) {
                AuthScreen(
                    onSyncAction = { navHostController.navigateSingleTopTo(route = Screen.SynchronizationScreen.route) },
                    modifier = modifier,
                )
            }
            composable(route = Screen.SynchronizationScreen.route) {
                SynchronizationScreen(
                    modifier = modifier,
                    onSyncronizedSucces = {
                        navHostController.navigateUp()
                    })
            }
        }
        navigation(startDestination = Screen.WordsListScreen.route, route = "MAIN") {
            composable(
                route = Screen.AddScreen.route,
                enterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                    )
                },
                popExitTransition = {
                    return@composable slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(200)
                    )
                },
            ) {
                AddWordsScreen(
                    navigateAfterSuccessWasAdded = { navHostController.navigateUp() },
                    modifier = modifier
                )
            }
            composable(route = Screen.WordsListScreen.route,
                enterTransition = {
                    return@composable fadeIn(tween(500))
                }, exitTransition = {
                    return@composable fadeOut(tween(200))
                }, popEnterTransition = {
                    return@composable slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(200)
                    )
                }) {
                ShowLearningWordsScreen(
                    onNavigate = { screen -> navHostController.navigate(screen.route) },
                    modifier = modifier
                )
            }
        }
    }


}