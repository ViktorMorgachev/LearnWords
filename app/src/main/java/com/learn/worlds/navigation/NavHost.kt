package com.learn.worlds.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learn.worlds.ui.add_word.AddWordsScreen
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
                AddWordsScreen(navigateAfterSuccessWasAdded = {
                    navHostController.popBackStack()
                })
            }
        }
        composable(route = Screen.LearnScreen.route) {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ShowLearningWordsScreen()
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
    }

}