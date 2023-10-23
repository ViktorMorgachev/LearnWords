package com.learn.worlds.ui.login.sync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.learn.worlds.R
import com.learn.worlds.ui.common.InformationDialog
import com.learn.worlds.ui.common.SomethingWentWrongDialog
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.ui.theme.fontFamilyAndroid


@Preview
@Composable
fun SynchronizationScreenPreview() {
    LearnWordsTheme{
       SynchronizationScreen(synchronizationState = SynchronizationState(), onSyncronizedSucces = {}, handleEvent = {})
    }
}

@Composable
fun SynchronizationScreen(modifier: Modifier = Modifier, synchronizationState: SynchronizationState, onSyncronizedSucces: ()->Unit,  handleEvent: (SynchronizationEvent)->Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_loading))

    var actualTestText by remember { mutableStateOf("DOWLOADING") }

    if (synchronizationState.success == true){
        onSyncronizedSucces.invoke()
    }

    if (synchronizationState.cancelledByUser == true){
        actualTestText = "Cancelled"
    }

    if (synchronizationState.emptyRemoteData == true){
        InformationDialog(message = stringResource(R.string.empty_data_from_network), onDismiss = { onSyncronizedSucces.invoke() })
    }

    synchronizationState.dialogError?.let {
        SomethingWentWrongDialog(message =  it.error, onDismiss = {
            handleEvent.invoke(SynchronizationEvent.DismissDialog)
            onSyncronizedSucces.invoke()
        })
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        Box(modifier = Modifier.background(Color(0xFF1ACC80)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LottieAnimation(composition,
                    modifier = modifier,
                    isPlaying = true,
                    iterations = LottieConstants.IterateForever)
                Spacer(Modifier.height(16.dp))
                Text(text = "DOWLOADING",
                    color = Color.White,
                    fontFamily = fontFamilyAndroid,
                    fontSize = 30.sp,
                    letterSpacing = (1.9).sp)
            }

        }
    }
    
}