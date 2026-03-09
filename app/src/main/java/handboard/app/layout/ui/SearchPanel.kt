package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.clipboard.ClipboardHistory
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground
import handboard.app.emoji.KaomojiData

sealed class SearchResult(val displayText: String, val category: String, val value: String) {
    class EmojiResult(emoji: String, name: String) : SearchResult("$emoji  $name", "Emoji", emoji)
    class KaomojiResult(kaomoji: String, cat: String) : SearchResult(kaomoji, "Kaomoji", kaomoji)
    class ClipboardResult(text: String) : SearchResult(text.take(60), "Clipboard", text)
}

class SearchEngine(private val clipboard: ClipboardHistory?) {
    private val emojiMap = mapOf(
        "smile" to "😊","laugh" to "😂","cry" to "😢","love" to "❤️","heart" to "❤️",
        "fire" to "🔥","thumbs" to "👍","ok" to "👌","wave" to "👋","clap" to "👏",
        "dog" to "🐶","cat" to "🐱","check" to "✅","cross" to "❌","party" to "🎉",
        "cool" to "😎","think" to "🤔","sleep" to "😴","angry" to "😡","sad" to "😢",
        "happy" to "😊","wink" to "😉","kiss" to "😘","money" to "💰","key" to "🔑"
    )

    fun search(q: String): List<SearchResult> {
        if (q.isBlank()) return emptyList()
        val query = q.lowercase().trim()
        val results = mutableListOf<SearchResult>()

        emojiMap.entries.filter { it.key.contains(query) }.take(6)
            .forEach { (n, e) -> results.add(SearchResult.EmojiResult(e, n)) }

        KaomojiData.categories.forEach { cat ->
            cat.items.filter { it.lowercase().contains(query) }.take(2)
                .forEach { results.add(SearchResult.KaomojiResult(it, cat.name)) }
        }

        clipboard?.items?.filter { it.text?.lowercase()?.contains(query) == true }?.take(3)
            ?.forEach { it.text?.let { t -> results.add(SearchResult.ClipboardResult(t)) } }

        return results.take(12)
    }
}

@Composable
fun SearchPanel(
    query: String,
    results: List<SearchResult>,
    heightScale: Float = 1f,
    onResultClick: (String) -> Unit,
    onClose: () -> Unit
) {
    val panelHeight = (120 * heightScale).dp

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(KeyboardBackground)) {
        // Search input (Fake TextField)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(KeyBackground).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchIcon(tint = KeyTextDim, size = 16.dp)
                Spacer(Modifier.width(8.dp))
                if (query.isEmpty()) {
                    Text("Type to search...", color = KeyTextDim, fontSize = 14.sp)
                } else {
                    Text(query, color = KeyText, fontSize = 14.sp)
                    // Fake cursor
                    Box(modifier = Modifier.padding(start = 2.dp).width(2.dp).height(16.dp).background(ShiftActiveBackground))
                }
            }
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ActionKeyBackground)
                    .clickable { onClose() }.semantics { contentDescription = "Close"; role = Role.Button },
                contentAlignment = Alignment.Center
            ) { Text("✕", color = KeyText, fontSize = 14.sp) }
        }

        // Results
        if (results.isEmpty() && query.isNotBlank()) {
            Box(Modifier.fillMaxWidth().height(panelHeight), contentAlignment = Alignment.Center) {
                Text("No results for \"$query\"", color = KeyTextDim, fontSize = 13.sp)
            }
        } else if (results.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(panelHeight),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(results) { r ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(KeyBackground)
                            .clickable { onResultClick(r.value) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(r.displayText, color = KeyText, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ActionKeyBackground.copy(alpha = 0.6f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(r.category, color = KeyTextDim, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
