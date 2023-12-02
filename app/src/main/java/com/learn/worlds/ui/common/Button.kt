package com.learn.worlds.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BaseButton(modifier: Modifier = Modifier, text: String, onClickAction: ()->Unit, enabled: Boolean = true) {
    Button(
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        onClick = {
            onClickAction.invoke()
        }) {
        Text(text = text)
    }
}

@Preview
@Composable
fun BaseButtonPreview() {
    BaseButton(text = "prewiew", onClickAction = {})
}