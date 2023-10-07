package com.learn.worlds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.learn.worlds.ui.add_word.LearningItemsViewModel
import com.learn.worlds.ui.theme.LearnWordsTheme

class MainActivity : ComponentActivity() {

    private val viewModel: LearningItemsViewModel by viewModels<LearningItemsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            LearnWordsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LearnWordsApp(viewModel)
                }

            }
        }
    }
}
