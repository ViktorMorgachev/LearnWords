package com.learn.worlds.ui.base.show_words

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.R
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.base.show_words.customization.getCardBackground
import com.learn.worlds.ui.base.show_words.customization.getCardTextColor
import com.learn.worlds.ui.common.ActionTopBar
import com.learn.worlds.ui.common.ActualTopBar
import com.learn.worlds.ui.common.InformationDialog
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import kotlinx.coroutines.launch
import timber.log.Timber


@Preview
@Composable
fun ShowLearningWordsScreenPreview() {
    MaterialTheme {
        ShowLearningWordsScreen(
            onNavigate = {},
            uiState = ShowWordsState(isAuthentificated = false)
        )
    }
}

@Composable
fun ShowLearningWordsScreen(
    modifier: Modifier = Modifier,
    viewModel: ShowLearningItemsViewModel = hiltViewModel(),
    uiState: ShowWordsState = viewModel.uiState.collectAsStateWithLifecycle().value,
    onNavigate: (Screen) -> Unit,
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val coroutineScope = rememberCoroutineScope()
        var showFilterMenu by remember { mutableStateOf(false) }
        var showSortMenu by remember { mutableStateOf(false) }


        uiState.error?.let {
            SomethingWentWrongDialog(
                message = it,
                onTryAgain = {
                    viewModel.dropErrorDialog()
                })
        }



        if (uiState.isLoading) {
            LoadingDialog()
        }

        LearningItemsScreen(modifier = modifier,
            isWasShowedLoginInformationDialog = viewModel.isShowedLoginInfoDialogForUser(),
            learningItems = uiState.learningItems,
            onChangeData = {},
            isAuthenticated = uiState.isAuthentificated,
            onLoginAction = { onNavigate.invoke(Screen.AuthScreen) },
            onShowedLoginInformationDialogAction = {
                viewModel.saveShowedLoginInfoDialog()
            },
            onSyncAction = {
                onNavigate.invoke(Screen.SynchronizationScreen)
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
                        )
                    )
                )
            }
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
                onShowedLoginInformationDialogAction = {},
                onChangeData = {},
                onLoginAction = {},
                onSyncAction = {},
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
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean? = null,
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem) -> Unit,
    appBar: @Composable (() -> Unit)? = null,
    onLoginAction: () -> Unit,
    onSyncAction: () -> Unit,
    isWasShowedLoginInformationDialog: Boolean = false,
    onShowedLoginInformationDialogAction: () -> Unit,
) {

    var isShowLoginInfoDialog by rememberSaveable { mutableStateOf(false) }

    Column {
        appBar?.invoke()
        if (isShowLoginInfoDialog) {
            InformationDialog(
                message = stringResource(R.string.information_login),
                onDismiss = {
                    onShowedLoginInformationDialogAction.invoke()
                    isShowLoginInfoDialog = false
                },
                onNextAction = {

                    onShowedLoginInformationDialogAction.invoke()
                    onLoginAction.invoke()
                    isShowLoginInfoDialog = false
                })
        }
        if (isAuthenticated == false) {
            NotAuthenticatedItem(
                onLoginAction = {
                    if (isWasShowedLoginInformationDialog) {
                        onLoginAction.invoke()
                    } else {
                        onShowedLoginInformationDialogAction.invoke()
                        isShowLoginInfoDialog = true
                    }
                }
            )
        }
        if (learningItems.isNotEmpty()) {
            LearningList(
                modifier = Modifier,
                learningItems = learningItems,
                onChangeData = onChangeData
            )
        } else EmptyScreen(
            isAuthenticated = isAuthenticated,
            modifier = modifier,
            onSyncAction = onSyncAction)
    }

}

@Preview
@Composable
fun NotAuthenticatedItemPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            NotAuthenticatedItem(onLoginAction = {})
        }
    }
}

@Composable
fun NotAuthenticatedItem(
    onLoginAction: () -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(0.1f))
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .weight(0.9f)
                .clickable {
                    onLoginAction.invoke()
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.action_sign_up),
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
        Spacer(modifier = Modifier.weight(0.1f))
    }

}


@Preview
@Composable
fun EmptyScreenPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        EmptyScreen(isAuthenticated = true, onSyncAction = {})
    }
}


@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean?,
    onSyncAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.empty_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (isAuthenticated == true) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = { onSyncAction.invoke() }) {
                    Text(text = stringResource(R.string.syncronize))
                }
            }


        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardContent(
    modifier: Modifier = Modifier,
    learningItem: LearningItem,
    onChangeData: (LearningItem) -> Unit,
    showDefaultNative: Boolean = true
) {
    var switch by rememberSaveable { mutableStateOf(false) }
    var switchAction = { switch = !switch }


    Crossfade(
        targetState = switch,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "crossroad_learning_card") { state ->
        if (!state) {
            if (showDefaultNative){
                CardItemNative(
                    learningItem = learningItem,
                    onClickedAction = switchAction)
            } else {
                CardItemForeign(  learningItem = learningItem,
                    onClickedAction = switchAction)
            }
        } else {
            if (showDefaultNative){
                CardItemForeign(  learningItem = learningItem,
                    onClickedAction = switchAction)
            } else {
                CardItemNative(learningItem = learningItem,
                    onClickedAction = switchAction)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardItemForeign(modifier: Modifier = Modifier, learningItem: LearningItem, onClickedAction: ()->Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = getCardBackground(
            isSystemDarkTheme = isSystemInDarkTheme(),
            foreignCard = true,
            learningStatus = learningItem.learningStatus
        )),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = {
            onClickedAction.invoke()
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
                Text(
                    text = learningItem.foreignData.lowercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = getCardTextColor(
                            isSystemDarkTheme = isSystemInDarkTheme(),
                            foreignCard = true,
                            learningStatus = learningItem.learningStatus
                        )
                    )
                )

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardItemNative(modifier: Modifier = Modifier, learningItem: LearningItem, onClickedAction: ()->Unit) {

    Card(
        colors = CardDefaults.cardColors(containerColor = getCardBackground(
            isSystemDarkTheme = isSystemInDarkTheme(),
            foreignCard = false,
            learningStatus = learningItem.learningStatus
        )),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = {
           onClickedAction.invoke()
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
                Text(
                    text = learningItem.nativeData,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = getCardTextColor(
                            isSystemDarkTheme = isSystemInDarkTheme(),
                            foreignCard = false,
                            learningStatus = learningItem.learningStatus
                        )
                    )
                )

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LearningList(
    modifier: Modifier = Modifier,
    learningItems: List<LearningItem>,
    onChangeData: (LearningItem) -> Unit,
    needRememberLastScrollState: Boolean = false
) {
    Timber.d("LearningList: Recompose: ${learningItems.joinToString(",\n")}")
    LazyColumn(
        state = if (needRememberLastScrollState) rememberLazyListState() else LazyListState(),
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(learningItems) { item ->
            CardContent(modifier = Modifier.animateItemPlacement(
                tween(durationMillis = 250)
            ), learningItem = item, onChangeData = onChangeData)
        }
    }
}