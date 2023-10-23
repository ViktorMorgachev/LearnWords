package com.learn.worlds.ui.base.subscribe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learn.worlds.R
import com.learn.worlds.ui.base.show_words.ShowLearningItemsViewModel
import com.learn.worlds.utils.stringRes


@Preview
@Composable
private fun SubscribeScreenPrewiew() {
    MaterialTheme {
        SubscribeScreen(onByCoffeeAction = {})
    }
}

@Composable
fun SubscribeScreen(modifier: Modifier = Modifier, onByCoffeeAction: ()->Unit, viewModel: ShowLearningItemsViewModel = hiltViewModel()) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = modifier.padding(12.dp), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringRes(R.string.subsribe_title),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                OutlinedButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        viewModel.dropLimits()
                        onByCoffeeAction.invoke()
                    }) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringRes(R.string.by_coffee))
                        Icon(
                            painter = painterResource(id = R.drawable.coffee),
                            contentDescription = null // decorative element
                        )
                    }
                }

            }
        }
    }
}