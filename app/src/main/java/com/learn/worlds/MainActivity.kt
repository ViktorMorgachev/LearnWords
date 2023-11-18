package com.learn.worlds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.theme.LearnWordsTheme
import com.learn.worlds.utils.AudioPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavHostController
    @Inject lateinit var audioPlayer: AudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LearnWordsTheme {
                navHostController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LearnWordsApp(navHostController, listOf(Screen.WordsListScreen, Screen.AddScreen))
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        audioPlayer.release()
    }
}
