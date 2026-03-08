package handboard.app.clipboard

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Build

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

    private val items = mutableListOf<ClipboardItem>()
    private val maxItems = 20
    private var clipboardManager: ClipboardManager? = null

    fun initialize() {
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboardManager?.addPrimaryClipChangedListener {
            readCurrentClip()
        }
        // Read current clip on init
        readCurrentClip()
    }

    private fun readCurrentClip() {
        try {
            val clip = clipboardManager?.primaryClip ?: return
            if (clip.itemCount == 0) return

            val item = clip.getItemAt(0)
            val text = item.text?.toString()
            val uri = item.uri

            // Determine if it's an image
            val description = clip.description
            val isImage = description != null &&
                (0 until description.mimeTypeCount).any { i ->
                    description.getMimeType(i)?.startsWith("image/") == true
                }

            val clipItem = if (isImage && uri != null) {
                val mimeType = if (description != null && description.mimeTypeCount > 0) {
                    description.getMimeType(0) ?: "image/*"
                } else "image/*"
                ClipboardItem(text = text, imageUri = uri, mimeType = mimeType)
            } else if (text != null && text.isNotBlank()) {
                ClipboardItem(text = text)
            } else {
                return
            }

            // Don't add duplicates
            if (items.isNotEmpty()) {
                val last = items.first()
                if (last.text == clipItem.text && last.imageUri == clipItem.imageUri) return
            }

            items.add(0, clipItem)
            if (items.size > maxItems) items.removeAt(items.lastIndex)

        } catch (_: Exception) {
            // Clipboard access can fail on some devices
        }
    }

    fun getItems(): List<ClipboardItem> = items.toList()

    fun removeItem(item: ClipboardItem) {
        items.remove(item)
    }

    fun clearAll() {
        items.clear()
    }
}
