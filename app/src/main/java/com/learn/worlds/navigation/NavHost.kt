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
import com.learn.worlds.ui.base.add_word.AddLearningItemsViewModel
import com.learn.worlds.ui.base.add_word.AddWordsScreen
import com.learn.worlds.ui.base.show_words.ShowLearningItemsViewModel
import com.learn.worlds.ui.base.show_words.ShowLearningWordsScreen
import com.learn.worlds.ui.base.subscribe.SubscribeScreen

@Composable
fun MyNavHost(
    navHostController: NavHostController,
    startDestination: String,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        route = "ROOT"
    ) {
        AuthGraph(navController = navHostController)
        MainGraph(navController = navHostController)
    }


}