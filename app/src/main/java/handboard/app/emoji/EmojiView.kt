package handboard.app.emoji

import android.util.TypedValue
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import handboard.app.core.theme.KeyboardBackground

@Composable
fun EmojiView(
    heightScale: Float = 1f,
    onEmojiClick: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val context = LocalContext.current
    val panelHeight = (240 * heightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        AndroidView(
            factory = {
                EmojiPickerView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            240f * heightScale,
                            context.resources.displayMetrics
                        ).toInt()
                    )
                    emojiGridColumns = 8
                    setOnEmojiPickedListener { emojiViewItem ->
                        onEmojiClick(emojiViewItem.emoji)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
        )
    }
}
