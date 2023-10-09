package com.learn.worlds

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.learn.worlds.navigation.LearningWordsScreens
import com.learn.worlds.navigation.MyNavHost

@Composable
fun LearnWordsApp(
    navController: NavHostController = rememberNavController()
) {
    MyNavHost(navController, startDestination = LearningWordsScreens.SCREEN_SHOW_WORDS.name)
}

