package com.learn.worlds.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.vector.ImageVector
import com.learn.worlds.R

data class BottomItem(val bottomIcon: ImageVector, @StringRes val bottomText: Int, @StringRes val description: Int? = null)
data class AppBarItem(@StringRes val title: Int?)

sealed class Screen(val route: String, val bottomItem: BottomItem? = null, val appBarItem: AppBarItem? = null) {
    object LearnScreen : Screen(
        route = "screen_learn",
        bottomItem = BottomItem(bottomIcon = Icons.Default.Language,  bottomText = R.string.learn),
        appBarItem = AppBarItem(title = R.string.learn ))
    object AddScreen : Screen(route = "screen_add",
        bottomItem = BottomItem(bottomIcon = Icons.Default.Add, bottomText = R.string.add))

    object SubscribeScreen : Screen(route = "screen_subscribe",
        appBarItem = AppBarItem(title = R.string.learn))
}