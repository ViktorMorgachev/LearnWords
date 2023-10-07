package com.learn.worlds.ui.show_words

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.ui.LearningItemsUIState as UIState
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog

@Composable
fun ShowLearningWordsScreen(modifier: Modifier = Modifier, viewModel: ShowLearningItemsViewModel = hiltViewModel()) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    when(items){
        is UIState.Loading-> LoadingDialog()
        is UIState.Error -> SomethingWentWrongDialog {
            viewModel.fetchLearningItemsData()
        }
        is UIState.Success -> {
            // TODO: Need to show list of all phrazes 
        }
    }
}