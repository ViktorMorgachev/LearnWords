package com.learn.worlds.ui.base.add_word

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.learn.worlds.R
import com.learn.worlds.ui.common.LoadingDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.getImageFile
import com.learn.worlds.utils.toBitmap
import kotlinx.coroutines.flow.MutableStateFlow

@Preview
@Composable
private fun AddWordsScreenPrewiew() {
    LearnWordsTheme {
        AddWordsUndependentScreen(
            uistate = AddWordsState(
                nativeText = MutableStateFlow("Что-то"),
                foreignText = MutableStateFlow("Something")
            ),
            navigateAfterSuccessWasAdded = {},
            onErrorDismissed = {},
            onStopPlayerAction = {},
            onForeignDataChanged = {},
            onNativeDataChanged = {},
            onInitCardData = {},
            onPlayAudioAction = {},
            onSaveCardData = {},
            onGetImageFile = {null})
    }
}
@Composable
fun AddWordsScreen(
    modifier: Modifier = Modifier,
    viewModel: AddLearningItemsViewModel = hiltViewModel(),
    uistate: AddWordsState = viewModel.uiState,
    context: Context = LocalContext.current,
    navigateAfterSuccessWasAdded: () -> Unit,
) {

    val handleEventMediator: (AddWordsEvent)->Unit = {
        viewModel.handleEvent(it)
    }

    AddWordsUndependentScreen(
        modifier = modifier,
        onPlayAudioAction = { handleEventMediator(AddWordsEvent.OnPlayAudio) },
        onStopPlayerAction = { handleEventMediator(AddWordsEvent.OnStopPlayer)},
        onForeignDataChanged = { handleEventMediator(AddWordsEvent.OnForeignDataChanged(it))},
        onNativeDataChanged = { handleEventMediator(AddWordsEvent.OnNativeDataChanged(it))},
        onErrorDismissed = { handleEventMediator(AddWordsEvent.OnErrorDismissed)},
        onGetImageFile = { getImageFile(it, context).toBitmap() },
        onInitCardData = { handleEventMediator(AddWordsEvent.InitCardData)},
        onSaveCardData = { handleEventMediator(AddWordsEvent.OnSaveLearningItem)},
        navigateAfterSuccessWasAdded = navigateAfterSuccessWasAdded,
        uistate = uistate)
}

@Composable
fun AddWordsUndependentScreen(
    modifier: Modifier = Modifier,
    onStopPlayerAction: () -> Unit,
    onErrorDismissed: () -> Unit,
    onInitCardData: () -> Unit,
    onSaveCardData: ()->Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onPlayAudioAction: () -> Unit,
    onForeignDataChanged: (String) -> Unit,
    onNativeDataChanged: (String) -> Unit,
    navigateAfterSuccessWasAdded: () -> Unit,
    onGetImageFile: (String?)->Bitmap?,
    uistate: AddWordsState = AddWordsState()
) {

    val errorsState = uistate.error.collectAsStateWithLifecycle().value
    val loadingState = uistate.isLoading.collectAsStateWithLifecycle().value
    val cardWasAdded = uistate.cardWasAdded.collectAsStateWithLifecycle().value
    val foreignText = uistate.foreignText.collectAsStateWithLifecycle().value
    val nativeText = uistate.nativeText.collectAsStateWithLifecycle().value
    val playerIsPlaying = uistate.playerIsPlaying.collectAsStateWithLifecycle().value
    val actualImageFileName = uistate.imageFile.collectAsStateWithLifecycle().value
    val speechFileName = uistate.speechFile.collectAsStateWithLifecycle().value


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onStopPlayerAction.invoke()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }



    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        errorsState?.let {
            SomethingWentWrongDialog(message = it, onDismiss = {
                onErrorDismissed.invoke()
            })
        }

        if (loadingState) {
            LoadingDialog()
        }

        if (cardWasAdded == true) {
            navigateAfterSuccessWasAdded.invoke()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .align(alignment = Alignment.TopCenter)
            ) {
                Column(
                    modifier = modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = "Слово на иностраном языке",
                        textAlign = TextAlign.Center
                    )
                    EditTextCustom(
                        actualText = foreignText,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {
                            onForeignDataChanged.invoke(it)
                        })
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Перевод",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    EditTextCustom(
                        actualText = nativeText,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = {onNativeDataChanged.invoke(it)})
                    Spacer(Modifier.height(16.dp))

                    PlayerButton(
                        onPlayAudioAction = { onPlayAudioAction.invoke() },
                        speechFileIsPresent = speechFileName != null,
                        playerIsPlaying = playerIsPlaying == true
                    )
                    Spacer(Modifier.height(16.dp))
                    CardImage(fileName = actualImageFileName, getImageFileAction = onGetImageFile)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlineButton(
                    text = "Предпросмотр",
                    onClick = { onInitCardData.invoke() },
                    enabled =  uistate.isCanToGenerate()
                )
                OutlineButton(
                    text = "Сохранить",
                    onClick = { onSaveCardData.invoke() },
                    enabled =  uistate.isCanToSave()
                )
            }
        }

    }

}

@Preview
@Composable
fun OutlineButton(modifier: Modifier = Modifier, onClick: ()->Unit = {}, enabled: Boolean = true, text: String = "") {
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black, disabledContentColor = Color.White, disabledContainerColor = Color.Gray),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}


@Composable
fun PlayerButton(
    modifier: Modifier = Modifier,
    onPlayAudioAction: () -> Unit,
    speechFileIsPresent: Boolean,
    playerIsPlaying: Boolean?
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.media_playing))

    val alphaTransition by animateFloatAsState(
        targetValue = if (playerIsPlaying == true) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "playingMediaAlpha",
    )

    AnimatedVisibility(
        visible = speechFileIsPresent,
        enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut(),
    ) {
        OutlinedButton(
            onClick = {
                onPlayAudioAction.invoke()
            },
            modifier = Modifier.height(38.dp)
        ) {
            Row(
                modifier = Modifier.width(110.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if (playerIsPlaying == false)
                        ImageVector.vectorResource(R.drawable.speaker)
                    else ImageVector.vectorResource(R.drawable.pause),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (playerIsPlaying == false)
                        "Прослушать" else "Пауза"
                )
                Spacer(
                    modifier = Modifier
                        .width(4.dp)
                        .weight(1f)
                )
                LottieAnimation(
                    modifier = Modifier
                        .size(26.dp)
                        .alpha(alphaTransition),
                    restartOnPlay = true,
                    composition = composition,
                    isPlaying = playerIsPlaying == true,
                    speed = 0.5f,
                    iterations = LottieConstants.IterateForever
                )
            }
        }
    }
}


@Composable
fun CardImage(modifier: Modifier = Modifier, fileName: String?, getImageFileAction: (String?)->Bitmap?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        getImageFileAction(fileName)?.let { bitmap->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Image(
                    modifier = Modifier
                        .size(256.dp, 256.dp)
                        .clip(ShapeDefaults.Medium),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "AIImage"
                )
            }
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