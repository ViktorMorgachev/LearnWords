package com.learn.worlds

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.learn.worlds.navigation.LearningWordsScreens
import com.learn.worlds.navigation.MyNavHost
import com.learn.worlds.ui.add_word.LearningItemsViewModel

@Composable
fun LearnWordsApp(
    viewModel: LearningItemsViewModel =  viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LearningWordsScreens.valueOf(
        backStackEntry?.destination?.route ?: LearningWordsScreens.SCREEN_SHOW_WORDS.name
    )
    // TODO: need to add floatingButton for add new data if user want 
    MyNavHost(navController, startDestination = LearningWordsScreens.SCREEN_SHOW_WORDS.name, modifier = Modifier.fillMaxSize(), learningItemsViewModel = viewModel)
}

