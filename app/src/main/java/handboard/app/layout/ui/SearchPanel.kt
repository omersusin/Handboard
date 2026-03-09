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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
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
        "pray" to "🙏","star" to "⭐","sun" to "☀️","moon" to "🌙","dog" to "🐶",
        "cat" to "🐱","check" to "✅","cross" to "❌","party" to "🎉","rocket" to "🚀",
        "cool" to "😎","think" to "🤔","sleep" to "😴","angry" to "😡","sad" to "😢",
        "happy" to "😊","wink" to "😉","kiss" to "😘","100" to "💯","sparkle" to "✨",
        "idea" to "💡","phone" to "📱","mail" to "📧","money" to "💰","key" to "🔑",
        "music" to "🎵","food" to "🍔","coffee" to "☕","pizza" to "🍕","car" to "🚗",
        "house" to "🏠","book" to "📖","pencil" to "✏️","target" to "🎯","trophy" to "🏆"
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
        KaomojiData.categories.filter { it.name.lowercase().contains(query) }.take(1)
            .forEach { cat -> cat.items.take(3).forEach { results.add(SearchResult.KaomojiResult(it, cat.name)) } }

        clipboard?.items?.filter { it.text?.lowercase()?.contains(query) == true }?.take(3)
            ?.forEach { it.text?.let { t -> results.add(SearchResult.ClipboardResult(t)) } }

        return results.take(12)
    }
}

@Composable
fun SearchPanel(
    heightScale: Float = 1f,
    clipboardHistory: ClipboardHistory?,
    onResultClick: (String) -> Unit,
    onClose: () -> Unit
) {
    val engine = remember(clipboardHistory) { SearchEngine(clipboardHistory) }
    var query by remember { mutableStateOf("") }
    val results = remember { mutableStateListOf<SearchResult>() }
    val focus = remember { FocusRequester() }
    val panelHeight = (200 * heightScale).dp

    LaunchedEffect(query) { results.clear(); if (query.isNotBlank()) results.addAll(engine.search(query)) }
    LaunchedEffect(Unit) { try { focus.requestFocus() } catch (_: Exception) {} }

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().background(KeyboardBackground)
    ) {
        // Search input
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KeyBackground)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchIcon(tint = KeyTextDim, size = 16.dp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) Text("Search...", color = KeyTextDim, fontSize = 14.sp)
                    BasicTextField(
                        value = query, onValueChange = { query = it },
                        textStyle = TextStyle(color = KeyText, fontSize = 14.sp),
                        cursorBrush = SolidColor(ShiftActiveBackground),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(focus)
                    )
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
                            .semantics { contentDescription = "Insert ${r.displayText}"; role = Role.Button }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(r.displayText, color = KeyText, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ActionKeyBackground.copy(alpha = 0.6f)).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(r.category, color = KeyTextDim, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxWidth().height(panelHeight), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SearchIcon(tint = KeyTextDim, size = 32.dp)
                    Spacer(Modifier.height(8.dp))
                    Text("Search emoji, kaomoji, clipboard", color = KeyTextDim, fontSize = 13.sp)
                    Text("Try: smile, heart, party, dog...", color = KeyTextDim.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
        }
    }
}
