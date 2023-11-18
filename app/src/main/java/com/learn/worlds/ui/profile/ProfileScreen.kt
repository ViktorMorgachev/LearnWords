package com.learn.worlds.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learn.worlds.ui.theme.LearnWordsTheme

@Preview
@Composable
fun ProfileScreenPreview() {
    LearnWordsTheme {
        ProfileScreen()
    }
}
//
/*
1. Отображать аватарку по дефолту - Две буквы имени
2. Отображать депозит на счету, сколько было потрачено на генерации новых карточек
 */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){

    }
}