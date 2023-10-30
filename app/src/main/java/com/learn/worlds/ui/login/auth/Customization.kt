package com.learn.worlds.ui.login.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getRequirementTintIcon(satisfied: Boolean): Color {
    return if (satisfied) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
}