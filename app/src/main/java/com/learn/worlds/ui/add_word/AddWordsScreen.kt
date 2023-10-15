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
    viewModel: AddLearningItemsViewModel = hiltViewModel()
) {
    var foreignData by rememberSaveable { mutableStateOf("") }
    var nativeData by rememberSaveable { mutableStateOf("") }
    var stateError by remember { mutableStateOf<Result.Error?>(null) }
    var stateLoadingState by remember { mutableStateOf<Boolean>(false) }
    var stateComplete by remember { mutableStateOf<Boolean?>(null) }

    val coroutineScope = rememberCoroutineScope()

    if (stateError != null) {
        SomethingWentWrongDialog(message = stateError!!.error,  { stateError = null }, { stateError = null })
    }

    if (stateLoadingState) {
        LoadingDialog({
            stateLoadingState = false
        })
    }

    if (stateComplete == true) {
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
            actualText = nativeData,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { nativeData = it })
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Введите перевод",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        EditTextCustom(
            actualText = foreignData,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { foreignData = it })
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            coroutineScope.launch {
                viewModel.addLearningItem(
                    LearningItem(
                        nativeData = nativeData.trimEnd(),
                        foreignData = foreignData.trimEnd()
                    )
                ).collectLatest {
                    when (it) {
                        is Result.Loading -> {
                            stateLoadingState = true
                            stateError = null
                        }

                        is Result.Error -> {
                            stateError = it
                            stateLoadingState = false
                        }

                        is Result.Complete -> {
                            stateLoadingState = false
                            stateError = null
                            stateComplete = true
                        }

                        else -> {}
                    }
                }


            }
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
            AddWordsScreen(navigateAfterSuccessWasAdded = {})
        }
    }
}