package com.learn.worlds.ui.show_words

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.learn.worlds.R
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.data.model.base.getActualText
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.common.ActionTopBar
import com.learn.worlds.ui.common.ActualTopBar
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.show_words.customization.getCardBackground
import com.learn.worlds.ui.show_words.customization.getCardTextColor
import com.learn.worlds.ui.theme.LearnWordsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowLearningWordsScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: ShowLearningItemsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val stateLearningItems by viewModel.stateLearningItems.collectAsStateWithLifecycle()

    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val error by viewModel.errorState.collectAsStateWithLifecycle()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }


    error?.let {
        SomethingWentWrongDialog(
            onTryAgain = {
                viewModel.dropErrorDialog()
            })
    }

    if (loadingState) {
        LoadingDialog()
    }

    LearningItemsScreen(modifier = modifier,
        learningItems = stateLearningItems,
        onChangeData = {
            coroutineScope.launch {
               // viewModel.changeLearningState(it.learningStatus, it.uid)
            }
        },
        appBar = {
            ActualTopBar(
                title = R.string.learn,
                actions = mutableListOf(
                    ActionTopBar(
                        imageVector = Icons.Default.FilterList,
                        contentDesc = R.string.desc_action_filter_list,
                        action = {
                            showFilterMenu = true
                        },
                        dropDownContent = {
                            DropdownMenu(
                                expanded = showFilterMenu,
                                onDismissRequest = { showFilterMenu = false }
                            ) {
                                Text(
                                    text = stringResource(R.string.filter_learned),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable(
                                            onClick = {
                                                showFilterMenu = false
                                                coroutineScope.launch {
                                                    viewModel.filterBy(FilteringType.LEARNED)
                                                }
                                            })
                                )
                                Text(
                                    text = stringResource(R.string.filter_all),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable(onClick = {
                                            showFilterMenu = false
                                            coroutineScope.launch {
                                                viewModel.filterBy(FilteringType.ALL)
                                            }
                                        })
                                )
                            }
                        }
                    ),
                    ActionTopBar(
                        imageVector = Icons.Default.Sort,
                        contentDesc = R.string.desc_action_sort_list,
                        action = {
                            showSortMenu = true
                        },
                        dropDownContent = {
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                Text(
                                    text = stringResource(R.string.sort_by_new),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable(onClick = {
                                            showSortMenu = false
                                            coroutineScope.launch {
                                                viewModel.sortBy(SortingType.SORT_BY_NEW)
                                            }

                                        })
                                )
                                Text(
                                    text = stringResource(R.string.sort_by_old),
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable(onClick = {
                                            showSortMenu = false
                                            coroutineScope.launch {
                                                viewModel.sortBy(SortingType.SORT_BY_OLD)
                                            }
                                        })
                                )
                            }
                        }
                    ),
                    ActionTopBar(
                        imageVector = Icons.Default.Sync,
                        contentDesc = R.string.desc_action_sort_list,
                        action = {

                        }
                    )
                ).apply {
                    if (viewModel.isLockedApplication()) {
                        add(
                            ActionTopBar(
                                imageVector = Icons.Default.LockOpen,
                                contentDesc = R.string.desc_action_filter_list,
                                action = {
                                    // TODO need to add navGraph for subscriptions userflow
                                    navHostController.navigate(Screen.SubscribeScreen.route)
                                }
                            ))
                    }
                }
            )
        }
    )
}

@Composable
fun FilteringMenu(
    onSelectedFilter: (FilteringType) -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        Text(
            text = stringResource(R.string.filter_all),
            modifier = Modifier
                .padding(10.dp)
                .clickable(
                    onClick = {
                        onDismissRequest.invoke()
                        onSelectedFilter.invoke(FilteringType.ALL)
                    })
        )
        Text(
            text = stringResource(R.string.filter_learned),
            modifier = Modifier
                .padding(10.dp)
                .clickable(onClick = {
                    onDismissRequest.invoke()
                    onSelectedFilter.invoke(FilteringType.LEARNED)
                })
        )
    }
}

@Preview
@Composable
private fun ShowLearningItemsScreenPreview() {
    LearnWordsTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LearningItemsScreen(
                learningItems = listOf(
                    LearningItem(
                        nativeData = "Девушка",
                        foreignData = "Girl",
                        learningStatus = LearningStatus.LEARNING.name
                    )
                ),
                onChangeData = {},
                appBar = {
                    ActualTopBar(
                        title = R.string.learn,
                        actions = listOf(
                            ActionTopBar(
                                imageVector = Icons.Default.FilterList,
                                contentDesc = R.string.desc_action_filter_list,
                                action = {},
                            ),
                            ActionTopBar(
                                imageVector = Icons.Default.Sort,
                                contentDesc = R.string.desc_action_sort_list,
                                action = {},
                            )
                        )
                    )
                }

            )
        }
    }
}

@Composable
fun LearningItemsScreen(
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem) -> Unit,
    modifier: Modifier = Modifier,
    appBar: @Composable (() -> Unit)? = null,
) {
    Column {
        appBar?.invoke()
        if (learningItems.isNotEmpty()) {
            LearningList(
                modifier = modifier,
                learningItems = learningItems,
                onChangeData = onChangeData
            )
            Text(text = "Sign In")
        } else EmptyScreen()
    }

}

@Preview
@Composable
fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.empty_list),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardContent(
    learningItem: LearningItem,
    onChangeData: (LearningItem) -> Unit,
    showDefaultNative: Boolean = true
) {
    var switch by rememberSaveable { mutableStateOf(false) }
    var actualText by rememberSaveable { mutableStateOf(learningItem.getActualText(showDefaultNative)) }

    val bgColor: Color by animateColorAsState(
        targetValue = getCardBackground(isSystemDarkTheme = isSystemInDarkTheme(), switch = switch, learningStatus = learningItem.learningStatus),
        animationSpec = tween(1000, easing = LinearEasing)
    )

    val textColor: Color by animateColorAsState(
        targetValue = getCardTextColor(isSystemDarkTheme = isSystemInDarkTheme(), switch = switch, learningStatus = learningItem.learningStatus),
        animationSpec = tween(1000, easing = LinearEasing)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = {
            switch = !switch
            if (switch) {
                actualText = learningItem.getActualText(!showDefaultNative)
            } else {
                actualText = learningItem.getActualText(showDefaultNative)
            }
        }
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Crossfade(targetState = switch) {
                    Text(
                        text = actualText.lowercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    )
                }

            }
        }
    }

}

@Composable
private fun LearningList(
    modifier: Modifier = Modifier,
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem) -> Unit,
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