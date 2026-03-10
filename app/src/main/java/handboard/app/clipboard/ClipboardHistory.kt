package handboard.app.clipboard

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf

data class ClipboardItem(
    val text: String? = null,
    val imageUri: Uri? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val mimeType: String = "text/plain"
) {
    val isImage: Boolean get() = imageUri != null
    val displayText: String get() = text ?: "[Image]"
}

class ClipboardHistory(private val context: Context) {

    private val maxItems = 20
    private var clipboardManager: ClipboardManager? = null
    private var listener: ClipboardManager.OnPrimaryClipChangedListener? = null

    val items = mutableStateListOf<ClipboardItem>()

    fun initialize() {
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        listener = ClipboardManager.OnPrimaryClipChangedListener { readCurrentClip() }
        clipboardManager?.addPrimaryClipChangedListener(listener)
        readCurrentClip()
    }

    fun destroy() {
        listener?.let { clipboardManager?.removePrimaryClipChangedListener(it) }
        listener = null
        clipboardManager = null
    }

    // ARTIK PUBLIC
    fun readCurrentClip() {
        try {
            val clip = clipboardManager?.primaryClip ?: return
            if (clip.itemCount == 0) return

            val item = clip.getItemAt(0)
            val text = item.text?.toString()
            val uri = item.uri

            val description = clip.description
            val isImage = description != null &&
                (0 until description.mimeTypeCount).any {
                    description.getMimeType(it)?.startsWith("image/") == true
                }

            val clipItem = if (isImage && uri != null) {
                val mime = description?.getMimeType(0) ?: "image/*"
                ClipboardItem(text = text, imageUri = uri, mimeType = mime)
            } else if (!text.isNullOrBlank()) {
                ClipboardItem(text = text)
            } else return

            if (items.isNotEmpty()) {
                val last = items.first()
                if (last.text == clipItem.text && last.imageUri == clipItem.imageUri) return
            }

            items.add(0, clipItem)
            if (items.size > maxItems) items.removeAt(items.lastIndex)
        } catch (_: Exception) { }
    }

    fun removeItem(item: ClipboardItem) { items.remove(item) }
    fun clearAll() { items.clear() }
}
