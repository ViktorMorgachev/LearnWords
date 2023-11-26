package com.learn.worlds.ui.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.learn.worlds.NavigationMediator

data class ActionTopBar(val imageVector: ImageVector, @StringRes val contentDesc: Int?, val action: ()->Unit, val dropDownContent: @Composable (() -> Unit)? = null)


sealed class IconLeftAppBar{
    data class NavMenuIcon(val imageVector: ImageVector = Icons.Filled.Menu, val action: ()->Unit = { NavigationMediator.open() }): IconLeftAppBar()
    data class NavBackIcon(val imageVector: ImageVector = Icons.Filled.ArrowBack, val action: ()->Unit = { NavigationMediator.popBackStack() }): IconLeftAppBar()
}

