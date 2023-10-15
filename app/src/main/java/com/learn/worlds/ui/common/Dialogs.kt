package com.learn.worlds.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Preview
@Composable
fun LoadingDialogPrewiew(
) {
    LoadingDialog()
}

@Composable
fun LoadingDialog(
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                shape = MaterialTheme.shapes.large,
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun SomethingWentWrongDialogPrewiew(
) {
    SomethingWentWrongDialog()
}

@Composable
fun SomethingWentWrongDialog(
    message: String? = null,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = message ?: "Somesthing went wrong dialog",
                    tint = Color.Red
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = message ?: "Somesthing went wrong")
                }

            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text("Try again")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

}