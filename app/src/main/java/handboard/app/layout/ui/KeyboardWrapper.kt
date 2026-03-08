package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import handboard.app.core.theme.KeyboardBackground

@Composable
fun KeyboardWrapper(
    widthFraction: Float,
    alignment: Int,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground),
        contentAlignment = when (alignment) {
            0 -> Alignment.BottomStart
            2 -> Alignment.BottomEnd
            else -> Alignment.BottomCenter
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .wrapContentHeight()
        ) {
            content()
        }
    }
}
