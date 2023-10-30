package com.learn.worlds.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class ActionTopBar(val imageVector: ImageVector, @StringRes val contentDesc: Int?, val action: ()->Unit, val dropDownContent: @Composable (() -> Unit)? = null)