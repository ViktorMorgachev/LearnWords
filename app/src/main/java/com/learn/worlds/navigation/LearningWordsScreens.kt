package com.learn.worlds.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.vector.ImageVector
import com.learn.worlds.R

data class BottomItem(val bottomIcon: ImageVector, @StringRes val bottomText: Int, @StringRes val description: Int? = null)
sealed class Screen(val route: String, val bottomItem: BottomItem? = null, val showDrawerIcon: Boolean = false) {
    object WordsListScreen : Screen(
        route = "screen_words_list",
        bottomItem = BottomItem(bottomIcon = Icons.Default.Language,  bottomText = R.string.list_of_words), showDrawerIcon = true)
    object AddScreen : Screen(route = "screen_add",
        bottomItem = BottomItem(bottomIcon = Icons.Default.Add, bottomText = R.string.add))

    object DemoScreen : Screen(route = "screen_demo",
        bottomItem = BottomItem(bottomIcon = Icons.Default.Cake, bottomText = R.string.demo))

    object SubscribeScreen : Screen(route = "screen_subscribe")

    object AuthScreen: Screen(route = "screen_auth")

    object PreferencesScreen: Screen(route = "screen_preferences")

    object  SynchronizationScreen: Screen(route = "sync_auth")
}