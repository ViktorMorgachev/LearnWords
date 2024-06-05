package com.learn.worlds.ui.demo.create_chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Preview(showBackground = true)
@Composable
fun CreateChatScreenPreview() {
    CreateChatScreen()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChatScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чаты") },
                actions = {
                    IconButton(onClick = { /* TODO: Handle button click */ }) {
                        Card(shape = RoundedCornerShape(size = 40.dp)) {
                            Icon(modifier = Modifier.padding(4.dp), imageVector =  Icons.Default.Add, contentDescription = "Add Chat")
                        }

                    }
                }
            )
        }
    ) { innerPadding ->

        if (false)
        ConstraintLayout(Modifier.padding(innerPadding)) {
            Text("Hello METANIT.COM", fontSize = 28.sp)
        }

        if (false)
        Column(modifier = Modifier
            .background(color = Color.Green)
            .fillMaxSize()) {
            Text(text = "Tlksjdflksjlkfjsdl;")
        }

        if (true)
        ConstraintLayout(modifier = Modifier
            .padding(vertical = 16.dp)
            .wrapContentHeight()
            .width(IntrinsicSize.Max)
            .padding(innerPadding)) {


            val startGuideline = createGuidelineFromStart(0.3f)
            // Create guideline from the end of the parent at 10% the width of the Composable
            val endGuideline = createGuidelineFromEnd(0.3f)

            val createChatTop = createRef()
            val createChatBottom = createRef()
            val createChatIcon = createRef()
            val iconCreateChatBtn = createRef()

           // createHorizontalChain(createChatTop, createChatBottom, chainStyle = ChainStyle.SpreadInside)
         //   createVerticalChain(createChatTop, createChatTop, createChatBottom, chainStyle = ChainStyle.Packed(1f))



            Card( modifier = Modifier.constrainAs(createChatIcon) {
                top.linkTo(parent.top, margin = 8.dp)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
                height = Dimension.wrapContent
            }, shape = RoundedCornerShape(size = 40.dp)) {
                Icon(modifier = Modifier.padding(16.dp), imageVector =  Icons.Default.ChatBubbleOutline, contentDescription = "Add Chat")
            }

            Text(
                modifier = Modifier
                    .constrainAs(createChatTop) {
                        top.linkTo(createChatIcon.bottom, margin = 8.dp)
                        end.linkTo(endGuideline)
                        start.linkTo(startGuideline)
                        bottom.linkTo(createChatBottom.top)
                        height = Dimension.wrapContent
                        width = Dimension.wrapContent
                },
                text = "Создай свой первый чат",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(
                modifier = Modifier
                    .constrainAs(createChatBottom) {
                        top.linkTo(createChatTop.bottom, margin = 8.dp)
                        end.linkTo(createChatTop.end)
                        start.linkTo(createChatTop.start)
                        width = Dimension.fillToConstraints
                    },
                maxLines = 3,
                text = "Общайтесь, отправляйте рекламные предложения, проводите тренинги",
                textAlign = TextAlign.Center
            )


            Button(
                modifier = Modifier
                    .constrainAs(iconCreateChatBtn) {
                        top.linkTo(createChatBottom.bottom, margin = 8.dp)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                        width = Dimension.preferredWrapContent
                    },
                onClick = { /* TODO: Handle button click */ }) {
                Text(modifier = Modifier, text = "Создать Чат")

            }
        }

        }

}

@Composable
fun ChatSection(title: String, description: String?, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = title, fontWeight = FontWeight.Bold)
                description?.let { Text(text = it) }
            }
        }
    }
}
