package handboard.app.clipboard

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ClipboardView(
    clipboardHistory: ClipboardHistory,
    heightScale: Float = 1f,
    onPasteText: (String) -> Unit,
    onPasteImage: (ClipboardItem) -> Unit,
    onClearAll: () -> Unit
) {
    val items = clipboardHistory.items
    val listHeight = (200 * heightScale).dp

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(KeyboardBackground)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Clipboard", color = KeyText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Row {
                Text(text = "Refresh", color = KeyTextDim, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ActionKeyBackground).clickable { clipboardHistory.readCurrentClip() }.padding(horizontal = 10.dp, vertical = 4.dp))
                Spacer(Modifier.width(8.dp))
                if (items.isNotEmpty()) {
                    Text(text = "Clear All", color = KeyTextDim, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ActionKeyBackground).clickable { onClearAll() }.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(listHeight), contentAlignment = Alignment.Center) { Text(text = "Clipboard is empty", color = KeyTextDim, fontSize = 14.sp) }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(listHeight), verticalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                items(items.toList(), key = { it.timestamp }) { item ->
                    ClipboardItemCard(item = item, onPasteText = onPasteText, onPasteImage = onPasteImage)
                }
            }
        }
    }
}

@Composable
private fun ClipboardItemCard(item: ClipboardItem, onPasteText: (String) -> Unit, onPasteImage: (ClipboardItem) -> Unit) {
    val context = LocalContext.current
    var bitmap by remember(item.imageUri) { mutableStateOf<ImageBitmap?>(null) }
    
    if (item.isImage && item.imageUri != null) {
        LaunchedEffect(item.imageUri) {
            bitmap = withContext(Dispatchers.IO) {
                try { context.contentResolver.openInputStream(item.imageUri)?.use { stream -> BitmapFactory.decodeStream(stream, null, BitmapFactory.Options().apply { inSampleSize = 4 })?.asImageBitmap() } } catch (_: Exception) { null }
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(KeyBackground).clickable { if (item.isImage) onPasteImage(item) else item.text?.let { onPasteText(it) } }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (bitmap != null) { Image(bitmap = bitmap!!, contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Crop); Spacer(modifier = Modifier.width(10.dp)) }
        Column(modifier = Modifier.weight(1f)) {
            if (item.isImage) {
                Text(text = "📷 Image", color = KeyText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                item.text?.let { Text(text = it, color = KeyTextDim, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            } else {
                Text(text = item.text ?: "", color = KeyText, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}
