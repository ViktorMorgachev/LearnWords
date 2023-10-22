package com.learn.worlds.ui.add_word

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


@Composable
fun AddWordsScreen(
    modifier: Modifier = Modifier,
    navigateAfterSuccessWasAdded: () -> Unit,
    addWordsState: AddWordsState,
    viewModel: AddLearningItemsViewModel = hiltViewModel()
) {

    val stateComplete by viewModel.stateWasSavedSuccessfully.collectAsStateWithLifecycle()


    addWordsState.error?.let {
        SomethingWentWrongDialog(message = it.error, onDismiss =  { viewModel.handleEvent(AddWordsEvent.ErrorDismissed) })
    }


    if (addWordsState.isLoading) {
        LoadingDialog()
    }

    if (stateComplete) {
        navigateAfterSuccessWasAdded.invoke()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Что хотите заучить?\n'Введите слово или фразу'",
            textAlign = TextAlign.Center
        )
        EditTextCustom(
            actualText = addWordsState.nativeData ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.handleEvent(AddWordsEvent.NativeDataChanged(nativeData = it)) })
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Введите перевод",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        EditTextCustom(
            actualText = addWordsState.foreignData ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.handleEvent(AddWordsEvent.ForeignDataChanged(foreignData = it)) })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.handleEvent(AddWordsEvent.SaveLearningItem)
        }
        ) {
            Text(text = "Сохранить")
        }
    }


}


@Composable
fun EditTextCustom(
    actualText: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        value = actualText,
        onValueChange = onValueChange,
        singleLine = true,
    )
}

@Preview
@Composable
private fun AddWordsScreenPreview() {
    LearnWordsTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddWordsScreen(navigateAfterSuccessWasAdded = {}, addWordsState = AddWordsState())
        }
    }
}