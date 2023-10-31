package com.learn.worlds.ui.base.show_words

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learn.worlds.R
import com.learn.worlds.data.model.base.FilteringType
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.base.SortingType
import com.learn.worlds.data.model.base.getActualText
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.base.show_words.customization.LearnItemTransitionData
import com.learn.worlds.ui.base.show_words.customization.getCardBackground
import com.learn.worlds.ui.base.show_words.customization.getCardTextColor
import com.learn.worlds.ui.common.ActionTopBar
import com.learn.worlds.ui.common.ActualTopBar
import com.learn.worlds.ui.common.InformationDialog
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import kotlinx.coroutines.launch
import kotlin.math.abs


val screenLabel = "show_words_screen"

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
            onSyncAction = onSyncAction
        )
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
    showDefaultNative: Boolean = true,
    maxLimitHorizontalOffset: Float,
    onDragState: (DraggableState) -> Unit,
) {
    val duration = 1000
    var switch by rememberSaveable { mutableStateOf(false) }

    val almostLimitPercent by remember { mutableStateOf(maxLimitHorizontalOffset * 0.1) }

    var stateAnimation by remember { mutableStateOf(false) }
    var targetValue by remember { mutableStateOf(0.0f) }
    val scope = rememberCoroutineScope()
    val cardOffset = remember { mutableStateOf(Offset(0.0f, 0.0f)) }
    val offsetTransition by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardOffsetTransition",
        finishedListener = {
            cardOffset.value = Offset(it, 0.0f)
            stateAnimation = false
        }
    )

    val transitionData = updateTransitionData(
        switchState = switch,
        learningItem = learningItem,
        duration = duration
    )

    var actualText by remember {
        mutableStateOf(
            learningItem.getActualText(
                showDefaultNative = showDefaultNative,
                switched = switch
            )
        )
    }

    val textAlpha by animateFloatAsState(
        animationSpec = tween(durationMillis = duration / 4, easing = LinearEasing),
        targetValue = if (transitionData.rotation == 0f || transitionData.rotation == 180f) {
            1f
        } else 0f,
        label = "animation_alpha"
    ) {
        if (it == 0f || it == 1f) {
            actualText = learningItem.getActualText(
                showDefaultNative = showDefaultNative,
                switched = switch
            )
        }
    }

    val switchAction = {
        switch = !switch
    }

    CardItem(
        modifier = modifier
            .offset {
                scope.launch {
                    val targetValueWithinBounds = offsetTransition.coerceIn(
                        -maxLimitHorizontalOffset,
                        maxLimitHorizontalOffset
                    )
                    if (abs(maxLimitHorizontalOffset) - abs(targetValueWithinBounds) <= almostLimitPercent) {
                        if (offsetTransition > 0) {
                            onDragState.invoke(DraggableState.RIGHT)
                        }
                        if (offsetTransition < 0) {
                            onDragState.invoke(DraggableState.LEFT)
                        }
                        return@launch
                    }
                    onDragState.invoke(DraggableState.CENTER)
                }

                IntOffset(
                    offsetTransition.toInt(),
                    0
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures() { change, dragAmount ->

                    if (offsetTransition == abs(maxLimitHorizontalOffset)) {
                        stateAnimation = false
                    }
                    val dragableDirection = when {
                        dragAmount > 0 -> {
                            DragDirection.TO_RIGHT
                        }
                        dragAmount < 0 -> {
                            DragDirection.TO_LEFT
                        }
                        else -> {
                            DragDirection.NONE
                        }
                    }

                    val offsetX = cardOffset.value.x

                    if (change.positionChange() != Offset.Zero) change.consume()
                    if (stateAnimation) return@detectHorizontalDragGestures
                    targetValue = computeActualTargetValue(
                        dragDirection = dragableDirection,
                        actualOffsetX = offsetX,
                        maxLimitHorizontalOffset = maxLimitHorizontalOffset
                    )
                    stateAnimation = true
                }
            }
            .graphicsLayer {
                rotationX = transitionData.rotation
            },
        state = switch,
        text = actualText,
        cardBackground = transitionData.background,
        textColor = transitionData.textColor,
        onClickedAction = switchAction,
        textAlpha = textAlpha
    )

}

@Composable
private fun updateTransitionData(
    switchState: Boolean,
    learningItem: LearningItem,
    duration: Int
): LearnItemTransitionData {
    val rootLabel = screenLabel + "_card_item"
    val transition = updateTransition(switchState, label = rootLabel)

    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = duration) }, label = rootLabel + "_rotation",
    ) { state -> if (state) 180f else 0f }

    val background by transition.animateColor(
        transitionSpec = { tween(durationMillis = duration) }, label = rootLabel + "_background",
    ) { state ->
        getCardBackground(
            isSystemDarkTheme = isSystemInDarkTheme(),
            foreignCard = state,
            learningStatus = learningItem.learningStatus
        )
    }

    val textColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = duration) }, label = rootLabel + "_background",
    ) { state ->
        getCardTextColor(
            isSystemDarkTheme = isSystemInDarkTheme(),
            foreignCard = state,
            learningStatus = learningItem.learningStatus
        )
    }

    return LearnItemTransitionData(
        rotation = rotation,
        background = background,
        textColor = textColor
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    text: String,
    textAlpha: Float,
    cardBackground: Color,
    textColor: Color,
    state: Boolean,
    onClickedAction: () -> Unit
) {
    val rootLabel = "card_item"

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
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
                Crossfade(
                    targetState = state,
                    animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
                    label = rootLabel + "_crossfade"
                ) { state ->
                    if (state) {
                        Text(
                            modifier = modifier,
                            text = text,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor.copy(alpha = textAlpha)
                            )
                        )
                    } else {
                        Text(
                            modifier = modifier,
                            text = text,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = textColor.copy(alpha = textAlpha)
                            )
                        )
                    }

                }


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
    LazyColumn(
        state = if (needRememberLastScrollState) rememberLazyListState() else LazyListState(),
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(learningItems) { item ->
            SwipeableCardItem(
                modifier = Modifier
                    .animateItemPlacement(
                        tween(durationMillis = 250)
                    ),
                learningItem = item,
                onChangeData = onChangeData,
                showDefaultNative = false
            )

        }
    }
}

@Composable
fun SwipeableCardItem(
    modifier: Modifier,
    dragLimitHorizontalPx: Float = 80.dp.dpToPx(),
    learningItem: LearningItem,
    onChangeData: (LearningItem) -> Unit,
    showDefaultNative: Boolean = true
) {
    var draggableState by remember { mutableStateOf(DraggableState.CENTER) }
    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        BackgroundSwipeable(
            modifier = modifier.fillMaxHeight(),
            draggableState = draggableState
        )
        CardContent(
            modifier = modifier,
            maxLimitHorizontalOffset = dragLimitHorizontalPx,
            onDragState = { draggableState = it },
            learningItem = learningItem,
            onChangeData = onChangeData,
            showDefaultNative = showDefaultNative
        )
    }

}

fun computeActualTargetValue(
    dragDirection: DragDirection,
    actualOffsetX: Float,
    maxLimitHorizontalOffset: Float
): Float {

    val actualState = if (actualOffsetX < 0) DraggableState.LEFT else
        if (actualOffsetX > 0) DraggableState.RIGHT else DraggableState.CENTER


    var targetOffset = when (actualState) {
        DraggableState.CENTER -> {
            when (dragDirection) {
                DragDirection.TO_RIGHT -> maxLimitHorizontalOffset
                DragDirection.TO_LEFT -> -maxLimitHorizontalOffset
                DragDirection.NONE -> 0.0f
            }
        }

        DraggableState.RIGHT -> {
            when (dragDirection) {
                DragDirection.TO_RIGHT -> maxLimitHorizontalOffset
                DragDirection.TO_LEFT -> 0.0f
                DragDirection.NONE -> maxLimitHorizontalOffset
            }
        }

        DraggableState.LEFT -> {
            when (dragDirection) {
                DragDirection.TO_RIGHT -> 0.0f
                DragDirection.TO_LEFT -> -maxLimitHorizontalOffset
                DragDirection.NONE -> -maxLimitHorizontalOffset

            }
        }
    }
    return targetOffset
}

@Composable
fun BackgroundSwipeable(modifier: Modifier, draggableState: DraggableState) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(
            visible = draggableState == DraggableState.RIGHT,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut(),
        ) {
            IconControlButton(
                modifier = modifier
                    .offset(x = (-10).dp)
                    .background(color = Color.Cyan, shape = RoundedCornerShape(14.dp))
                    .padding(start = 20.dp, end = 8.dp),
                icon = Icons.Outlined.Delete,
                contentDescription = "Delete item",
                onClick = {},
                tintColor = Color.Black
            )
        }

        IconControlButton(
            icon = Icons.Outlined.Abc,
            contentDescription = "Delete item",
            onClick = {},
            tintColor = Color.Blue.copy(alpha = 0.0f)
        )

        AnimatedVisibility(
            visible = draggableState == DraggableState.LEFT,
            enter = slideInHorizontally() + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut(),
        ) {
            IconControlButton(
                modifier = modifier
                    .offset(x = (10).dp)
                    .background(color = Color.Cyan, shape = RoundedCornerShape(14.dp))
                    .padding(end = 20.dp, start = 8.dp),
                icon = Icons.Outlined.Edit,
                contentDescription = "",
                onClick = {},
                tintColor = Color.Black
            )
        }


    }
}

@Composable
private fun IconControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tintColor: Color = Color.White,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

enum class DragDirection {
    TO_RIGHT, TO_LEFT, NONE
}

enum class DraggableState {
    CENTER, RIGHT, LEFT
}


@Composable
private fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }