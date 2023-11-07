package com.learn.worlds.ui.base.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
private fun DemoScreen2(modifier: Modifier = Modifier) {

    val movableContent = remember {
        movableContentOf {
            Box(modifier = Modifier.padding(4.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(color = Color.Blue, shape = MaterialTheme.shapes.small)
                .shadow(1.dp, shape = MaterialTheme.shapes.small)
                .size(50.dp))
            Box(modifier = Modifier.padding(4.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(color = Color.Green, shape = MaterialTheme.shapes.small)
                .shadow(1.dp, shape = MaterialTheme.shapes.small)
                .size(30.dp))
            Box(modifier = Modifier.padding(4.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(color = Color.Red, shape = MaterialTheme.shapes.small)
                .shadow(1.dp, shape = MaterialTheme.shapes.small)
                .size(20.dp))
            Box(modifier = Modifier.padding(4.dp)
                .clip(shape = MaterialTheme.shapes.small)
                .background(color = Color.Yellow, shape = MaterialTheme.shapes.small)
                .shadow(1.dp, shape = MaterialTheme.shapes.small)
                .size(10.dp))
        }
    }
    var counter by remember { mutableStateOf(2) }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {counter++}){
                Text(text =  "Change item")
            }
            when(counter % 3){
                0-> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        movableContent()
                    }

                }
                1->{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        movableContent()
                    }
                }
                2->{
                    Box(contentAlignment = Alignment.Center) {
                        movableContent()
                    }
                }
            }
        }
    }
}