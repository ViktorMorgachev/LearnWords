package com.learn.worlds.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

@Composable
fun LabelingEditText(
    modifier: Modifier = Modifier,
    label: String,
    actualText: String,
    maxChars: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium
) {
    OutlinedTextField(
        modifier = modifier,
        label = {
            Text(text = label)
        },
        textStyle = TextStyle.Default.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        enabled = enabled,
        shape = shape,
        value = actualText,
        onValueChange = {
            if (it.length <= maxChars) onValueChange.invoke(it)
        },
        singleLine = true,
    )
}