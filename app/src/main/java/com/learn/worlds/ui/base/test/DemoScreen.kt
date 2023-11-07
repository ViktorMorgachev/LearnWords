
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun DemoScreenPrewiew() {
    MaterialTheme {
        DemoScreen()
    }
}

@Composable
fun DemoScreen(modifier: Modifier = Modifier) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    }
}