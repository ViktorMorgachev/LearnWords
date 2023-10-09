package com.learn.worlds.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.learn.worlds.navigation.Screen
import com.learn.worlds.utils.stringRes

@Composable
fun BottomBar(
    screens: List<Screen>,
    currentDestination: NavDestination?,
    navController: NavController
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        screens.filter { it.bottomItem != null }.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = screen.bottomItem!!.bottomIcon, contentDescription = stringRes(screen.bottomItem.description))
                },
                label = {
                    Text(text = stringRes(screen.bottomItem!!.bottomText))
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }

    }
}