package com.learn.worlds
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.learn.worlds.navigation.MyNavHost
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.common.BottomBar

@Composable
fun LearnWordsApp(navController: NavHostController, allScreens: List<Screen>) {
    val mainBottomsScreens = allScreens.filter { it.bottomItem != null }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val shouldShowBottomBar = navBackStackEntry?.destination?.route in mainBottomsScreens.map { it.route }
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar){
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                    val currentDestination = navBackStackEntry?.destination
                    BottomBar(screens = mainBottomsScreens, currentDestination = currentDestination, navController = navController)
                }
            }
        }
    ) { innerPadding ->
        MyNavHost(navController, startDestination = Screen.LearnScreen.route, modifier = Modifier.padding(innerPadding))
    }

}

