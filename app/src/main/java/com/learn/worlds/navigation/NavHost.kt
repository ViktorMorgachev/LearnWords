package com.learn.worlds.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learn.worlds.ui.add_word.AddWordsScreen
import com.learn.worlds.ui.show_words.ShowLearningWordsScreen

@Composable
fun MyNavHost(
    navHostController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(route = LearningWordsScreens.SCREEN_ADD_NEW_WORDS.name) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AddWordsScreen()
            }
        }
        composable(route = LearningWordsScreens.SCREEN_SHOW_WORDS.name) {
            ShowLearningWordsScreen()
        }
    }

}