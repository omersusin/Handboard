package handboard.app.clipboard

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground

@Composable
fun ClipboardView(
    clipboardHistory: ClipboardHistory,
    heightScale: Float = 1f,
    onPasteText: (String) -> Unit,
    onPasteImage: (ClipboardItem) -> Unit,
    onClearAll: () -> Unit
) {
    val items = remember { clipboardHistory.getItems() }
    val listHeight = (200 * heightScale).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Clipboard",
                color = KeyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (items.isNotEmpty()) {
                Text(
                    text = "Clear All",
                    color = KeyTextDim,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ActionKeyBackground)
                        .clickable { onClearAll() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(listHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Clipboard is empty",
                    color = KeyTextDim,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(listHeight),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
            ) {
                items(items) { item ->
                    ClipboardItemCard(
                        item = item,
                        onPasteText = onPasteText,
                        onPasteImage = onPasteImage
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardItemCard(
    item: ClipboardItem,
    onPasteText: (String) -> Unit,
    onPasteImage: (ClipboardItem) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(KeyBackground)
            .clickable {
                if (item.isImage) {
                    onPasteImage(item)
                } else if (item.text != null) {
                    onPasteText(item.text)
                }
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.isImage && item.imageUri != null) {
            val bitmap = remember(item.imageUri) {
                try {
                    context.contentResolver.openInputStream(item.imageUri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.asImageBitmap()
                    }
                } catch (_: Exception) { null }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Clipboard image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            if (item.isImage) {
                Text(
                    text = "📷 Image",
                    color = KeyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                if (item.text != null) {
                    Text(
                        text = item.text,
                        color = KeyTextDim,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text(
                    text = item.text ?: "",
                    color = KeyText,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
