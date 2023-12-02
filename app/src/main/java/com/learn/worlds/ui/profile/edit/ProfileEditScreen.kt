package com.learn.worlds.ui.profile.edit

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.R
import com.learn.worlds.ui.common.BaseButton
import com.learn.worlds.ui.common.LabelingEditText
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.Result

val profileEditState = ProfileEditState(loadingState = false, somethingWentWrongState = false)

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
    uiState: ProfileEditState = viewModel.uiState.collectAsStateWithLifecycle().value,
) {
    ProfileEditScreenPreview(
        onFirstNameChanged = {
            viewModel.handleEvent(ProfileEditEvent.onChangeFirstNameEvent(it))
        },
        onSecondNameChanged = {
            viewModel.handleEvent(ProfileEditEvent.onChangeSecondNameEvent(it))
        },
        onDismissErrorDialog = {
            viewModel.handleEvent(ProfileEditEvent.onDismissErrorDialogEvent)
        },
        onSaveButtonAction = {
            viewModel.handleEvent(ProfileEditEvent.onSaveProfileEvent)
        },
        state = uiState
    )
}

@Composable
fun ProfileEditScreenDefault() {
    ProfileEditScreenPreview(
        modifier = Modifier.fillMaxWidth(),
        onSecondNameChanged = {},
        onFirstNameChanged = {},
        onSaveButtonAction = {},
        state = profileEditState
    )
}

@Composable
fun ProfileEditScreenPreview(
    modifier: Modifier = Modifier,
    onFirstNameChanged: (String) -> Unit = {},
    onSecondNameChanged: (String) -> Unit = {},
    onSaveButtonAction: () -> Unit = {},
    onDismissErrorDialog: () -> Unit = {},
    state: ProfileEditState
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        if (state.loadingState) {
            LoadingDialog()
        }
        if (state.somethingWentWrongState) {
            SomethingWentWrongDialog(message = Result.Error(), onDismiss = {
                onDismissErrorDialog.invoke()
            })
        }
        Column(modifier = modifier) {
            Card(modifier = modifier.padding(16.dp)) {
                Column(modifier = modifier.padding(16.dp)) {
                    LabelingEditText(
                        modifier = modifier.fillMaxWidth(),
                        label = stringResource(R.string.profile_edit_input_first_name),
                        actualText = state.firstName,
                        maxChars = 20,
                        onValueChange = { onFirstNameChanged.invoke(it) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LabelingEditText(
                        modifier = modifier.fillMaxWidth(),
                        label = stringResource(R.string.profile_edit_input_second_name),
                        actualText = state.secondName,
                        maxChars = 20,
                        onValueChange = { onSecondNameChanged.invoke(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BaseButton(modifier = Modifier.fillMaxWidth(),
                    enabled = state.firstName.isNotEmpty() && state.secondName.isNotEmpty(),
                    text = stringResource(R.string.save),
                    onClickAction = { onSaveButtonAction.invoke() })
            }

        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun ProfileEditScreenDark() {
    LearnWordsTheme {
        ProfileEditScreenDefault()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun ProfileEditScreenLight() {
    LearnWordsTheme {
        ProfileEditScreenDefault()
    }
}