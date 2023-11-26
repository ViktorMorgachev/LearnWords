package com.learn.worlds.ui.progress

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learn.worlds.ui.theme.LearnWordsTheme

@Preview
@Composable
fun ProgressScreenPreview() {
    LearnWordsTheme {
        ProgressScreen()
    }
}

@Composable
fun ProgressScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

    }
}