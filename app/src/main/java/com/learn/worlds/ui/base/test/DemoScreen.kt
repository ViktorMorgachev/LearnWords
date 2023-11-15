
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learn.worlds.ui.login.auth.AuthenticationMode
import com.learn.worlds.ui.login.auth.RequirementForm

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
        Column {
            EditTextCustom(actualText = "gragon", onValueChange = {}, suggestion = "dragon")
        }

    }
}

@Composable
fun EditTextCustom(
    actualText: String,
    suggestion: String? = null,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {

    Box(){
        AnimatedVisibility(
            visible = !suggestion.isNullOrEmpty(),
            enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut(),
        ) {
            SpellingText(
                successState = false,
                text = actualText, modifier = Modifier.offset(x = 6.dp, y = 6.dp))
        }

        OutlinedTextField(

            textStyle = TextStyle.Default.copy(
                color = Color.Black),
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            value = if (suggestion.isNullOrEmpty()) actualText else "",
            onValueChange = onValueChange,
            singleLine = true,
            supportingText = {
                Box(propagateMinConstraints = true , modifier = modifier
                    .offset(y = (-4).dp, x = (-4).dp)
                    .padding(vertical = 4.dp)
                    ){
                    suggestion?.let {
                        SpellingText(text = it, successState = true)
                    }

                }

            }
        )
    }

}


@Composable
fun SpellingText(modifier: Modifier = Modifier, text: String, successState: Boolean) {

    val actualSuggestionBackgroundColors by remember { mutableStateOf(CustomisationSpellingTextBackground(successState)) }
    val localDensity = LocalDensity.current
    var conteinerHeight by remember { mutableStateOf(0f) }
    var containerWidth by remember { mutableStateOf(0f) }

    Box(modifier = modifier.onGloballyPositioned { coordinates ->
        conteinerHeight = with(localDensity) { coordinates.size.height.toFloat() }
        containerWidth = with(localDensity) { coordinates.size.width.toFloat() }
    }){
        Text(
            text = text,
            color = Color.Black,
            modifier = modifier
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        listOf(
                            actualSuggestionBackgroundColors.first,
                            actualSuggestionBackgroundColors.first
                        )
                    )
                    onDrawBehind {
                        drawRoundRect(
                            brush = brush,
                            cornerRadius = CornerRadius(7.dp.toPx())
                        )
                    }

                }
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        listOf(
                            actualSuggestionBackgroundColors.second,
                            actualSuggestionBackgroundColors.second
                        )
                    )
                    onDrawBehind {
                        drawRoundRect(
                            size = Size(
                                width = containerWidth,
                                height = conteinerHeight * 0.9f
                            ),
                            brush = brush,
                            cornerRadius = CornerRadius(7.dp.toPx())
                        )
                    }
                }
                .padding(horizontal = 4.dp, vertical = 4.dp)
        )
    }


}

fun CustomisationSpellingTextBackground(successState: Boolean): Pair<Color, Color>  {

  return  if (successState){
        Color(0xFF28A745) to Color(0xFF1EF1B3)
    } else {
        Color(0xffdc3545) to Color(0xfff86c6b)
    }

}