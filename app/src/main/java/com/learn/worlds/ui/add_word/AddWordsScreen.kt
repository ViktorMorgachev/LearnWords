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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.ui.LearningItemsUIState
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.show_words.ShowLearningItemsViewModel
import com.learn.worlds.ui.theme.LearnWordsTheme

@Composable
fun AddWordsScreen(modifier: Modifier = Modifier, viewModel: AddLearningItemsViewModel = hiltViewModel()) {
    var foreignData by remember { mutableStateOf("") }
    var nativeData by remember { mutableStateOf("") }
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    when(items){
        is LearningItemsUIState.Loading-> LoadingDialog()
        is LearningItemsUIState.Error -> SomethingWentWrongDialog {
            viewModel.addLearningItem(LearningItem(nativeData = nativeData, foreignData = foreignData))
        }
        is LearningItemsUIState.Success -> {
            // TODO: Need close this screen and navigate to previous screen
        }
    }

    Column(
        modifier = Modifier
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
        EditTextCustom(actualText = nativeData, modifier = Modifier.fillMaxWidth(), onValueChange = {nativeData = it})
        Spacer(Modifier.height(16.dp))
        Text(text = "Введите перевод", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp))
        EditTextCustom(actualText = foreignData, modifier = Modifier.fillMaxWidth(), onValueChange = {foreignData = it})
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.addLearningItem(LearningItem(nativeData = nativeData, foreignData = foreignData)) }) {
            Text(text = "Сохранить")
        }
    }


}


@Composable
fun EditTextCustom(actualText: String,
                   modifier: Modifier = Modifier,
                   onValueChange: (String) -> Unit) {
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
            AddWordsScreen()
        }
    }
}