package com.learn.worlds.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.learn.worlds.R
import com.learn.worlds.utils.Result

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
fun SuccessDialogPrewiew(
) {
    SuccessDialog(message = "Успешно зарегистрировались в системе")
}


@Preview
@Composable
fun InfoDialogPrewiew(
) {
    InformationDialog(message = "Информация")
}

@Composable
fun InformationDialog(
    message: String,
    onDismiss: () -> Unit = {},
    onNextAction: (() -> Unit)? = null
) {
    Surface(modifier = Modifier,
        shadowElevation = 16.dp) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = message,
                    tint = Color.Blue
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = message)
                }

            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                onNextAction?.let {
                    TextButton(
                        onClick = {
                            it.invoke()
                        }
                    ) {
                        Text(text = stringResource(R.string.next))
                    }
                }

            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }

}

@Composable
fun SuccessDialog(
    message: String,
    onDismiss: () -> Unit = {}
) {
    Surface(modifier = Modifier,
        shadowElevation = 16.dp) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = message,
                    tint = Color.Green
                )
            },
            title = {
                Text(text = stringResource(R.string.well))
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = message)
                }

            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }

}

@Preview
@Composable
fun SomethingWentWrongDialogPrewiew(
) {
    SomethingWentWrongDialog(Result.Error())
}

@Composable
fun SomethingWentWrongDialog(
    message: Result.Error,
    onDismiss: () -> Unit = {},
    onTryAgain: (() -> Unit)? = null
) {


    Surface(modifier = Modifier,
        shadowElevation = 16.dp) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = stringResource(message.errorType.resID),
                    tint = Color.Red
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(message.errorType.resID))
                }

            },
            onDismissRequest = {
                onDismiss()
            },
            confirmButton = {
                onTryAgain?.let {
                    TextButton(
                        onClick = {
                            onTryAgain()
                        }
                    ) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }

}