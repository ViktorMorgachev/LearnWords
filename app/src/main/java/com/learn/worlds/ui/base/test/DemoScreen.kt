
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@Composable
private fun DemoScreenPrewiew() {
    MaterialTheme {
        DemoScreen()
    }
}

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {

    var fruits by remember { mutableStateOf<List<String>>(listOf<String>("Apple", "Mango", "Banana", "Orange", "Watermelon", "Papaya")) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            itemsIndexed(fruits){index, item ->
                ClickableText(
                    text = AnnotatedString("${index+1}. $item"),
                    onClick = {
                       fruits =  fruits.toMutableList().apply {
                           remove(item)
                       }
                    }
                )
            }
        }
    }
}