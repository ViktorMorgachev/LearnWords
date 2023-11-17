package com.learn.worlds.ui.common

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun OutlineButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    text: String = ""
) {
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Gray
        ),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}