package com.learn.worlds.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.learn.worlds.R

@Composable
fun ChangeStatusDialog(
    onDismiss: () -> Unit = {},
    onAgreeAction: (() -> Unit)? = null
) {


    Surface(modifier = Modifier,
        shadowElevation = 16.dp) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(R.string.change_status_learned),
                    tint = Color.Red
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(textAlign = TextAlign.Center, text = stringResource(R.string.change_status_learned))
                }

            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAgreeAction?.invoke()
                    }
                ) {
                    Text(text = stringResource(R.string.yes))
                }
            }
        )
    }

}