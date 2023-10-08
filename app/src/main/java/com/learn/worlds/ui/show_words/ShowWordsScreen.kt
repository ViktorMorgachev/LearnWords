package com.learn.worlds.ui.show_words

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.ui.LearningItemsUIState
import com.learn.worlds.ui.LearningItemsUIState as UIState
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme

@Composable
fun ShowLearningWordsScreen(modifier: Modifier = Modifier, viewModel: ShowLearningItemsViewModel = hiltViewModel()) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    when(items){
        is UIState.Loading-> LoadingDialog()
        is UIState.Error -> SomethingWentWrongDialog {
            viewModel.fetchLearningItemsData()
        }
        is UIState.Success -> {
            LearningItemsScreen(modifier = modifier, learningItems = (items as LearningItemsUIState.Success).data, onChangeData = {
                viewModel.changeLearningState(it.learningStatus, it.uid)
            })
        }
    }
}

@Preview
@Composable
private fun ShowLearningItemsScreenPreview() {
    LearnWordsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LearningItemsScreen(
              learningItems = listOf(),
                onChangeData = {}
            )
        }
    }
}

@Composable
fun LearningItemsScreen(
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem)->Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        // TODO: Add filter button and floating button for adding new data
    ) { innerPadding ->
        LearningList(
            modifier = modifier
                .padding(innerPadding),
            learningItems = learningItems,
            onChangeData = onChangeData,
        )
    }
}

@Composable
fun CardContent(learningItem: LearningItem, onChangeData: (LearningItem)->Unit, showDefaultNative: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = if (showDefaultNative) learningItem.nativeData else learningItem.foreignData, style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                if (expanded) {
                    Text(
                        text =  if (showDefaultNative) learningItem.foreignData else learningItem.nativeData
                    )
                }
            }
            IconButton(onClick = {
                if (!expanded){
                    onChangeData.invoke(learningItem.copy(learningStatus = LearningStatus.LEARNING.name))
                }
                expanded = !expanded

            }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = ""
                )
            }
        }
    }

}

@Composable
private fun LearningList(
    modifier: Modifier = Modifier,
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem)->Unit,
    needRememberLastScrollState: Boolean = true
) {
    LazyColumn(
        state = if (needRememberLastScrollState) rememberLazyListState() else LazyListState(),
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(learningItems) { item ->
            CardContent(learningItem = item, onChangeData = onChangeData)
        }
    }
}