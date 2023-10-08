package com.learn.worlds

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.learn.worlds.navigation.LearningWordsScreens
import com.learn.worlds.navigation.MyNavHost

@Composable
fun LearnWordsApp(
    navController: NavHostController = rememberNavController()
) {
   /* // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen for bottom navigation
    val currentScreen = LearningWordsScreens.valueOf(
        backStackEntry?.destination?.route ?: LearningWordsScreens.SCREEN_SHOW_WORDS.name
    )*/

    MyNavHost(navController, startDestination = LearningWordsScreens.SCREEN_SHOW_WORDS.name)
}

