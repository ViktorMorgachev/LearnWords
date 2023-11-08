
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learn.worlds.ui.base.add_word.AddWordsState

@Preview
@Composable
private fun DemoScreenPrewiew() {
    MaterialTheme {

    }
}

@Composable
fun DemoScreen(modifier: Modifier = Modifier,
               addWordsState: AddWordsState,
               onChangeNativeData: (String)->Unit,
               onChangeForeignData: (String)->Unit,
               onSaveItem: ()->Unit) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }
}