package com.learn.worlds.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.vector.ImageVector
import com.learn.worlds.R


sealed class Screen(val route: String, @StringRes val screenResId: Int, val bottomIcon: ImageVector) {
    object LearnScreen : Screen("screen_learn", R.string.learn, Icons.Default.Language)
    object AddScreen : Screen("screen_add", R.string.add, Icons.Default.Add)
}