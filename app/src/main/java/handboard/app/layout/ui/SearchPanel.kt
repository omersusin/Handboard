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

sealed class SearchResult(
    val displayText: String,
    val category: String,
    val value: String
) {
    class EmojiResult(emoji: String, name: String) :
        SearchResult(displayText = "$emoji  $name", category = "Emoji", value = emoji)

    class KaomojiResult(kaomoji: String, categoryName: String) :
        SearchResult(displayText = kaomoji, category = "Kaomoji · $categoryName", value = kaomoji)

    class ClipboardResult(text: String) :
        SearchResult(
            displayText = text.take(80),
            category = "Clipboard",
            value = text
        )
}

class SearchEngine(
    private val clipboardHistory: ClipboardHistory?
) {
    // Common emoji search map
    private val emojiSearch = mapOf(
        "smile" to "😊", "laugh" to "😂", "cry" to "😢", "love" to "❤️",
        "heart" to "❤️", "fire" to "🔥", "thumbs" to "👍", "ok" to "👌",
        "wave" to "👋", "clap" to "👏", "pray" to "🙏", "strong" to "💪",
        "star" to "⭐", "sun" to "☀️", "moon" to "🌙", "rain" to "🌧️",
        "snow" to "❄️", "cloud" to "☁️", "tree" to "🌳", "flower" to "🌸",
        "dog" to "🐶", "cat" to "🐱", "fish" to "🐟", "bird" to "🐦",
        "check" to "✅", "cross" to "❌", "warning" to "⚠️", "stop" to "🛑",
        "party" to "🎉", "cake" to "🎂", "gift" to "🎁", "music" to "🎵",
        "phone" to "📱", "mail" to "📧", "clock" to "⏰", "key" to "🔑",
        "lock" to "🔒", "search" to "🔍", "money" to "💰", "car" to "🚗",
        "plane" to "✈️", "house" to "🏠", "food" to "🍔", "pizza" to "🍕",
        "coffee" to "☕", "beer" to "🍺", "wine" to "🍷", "water" to "💧",
        "rocket" to "🚀", "flag" to "🏁", "trophy" to "🏆", "medal" to "🏅",
        "bomb" to "💣", "gem" to "💎", "ring" to "💍", "crown" to "👑",
        "robot" to "🤖", "alien" to "👽", "ghost" to "👻", "skull" to "💀",
        "poop" to "💩", "devil" to "😈", "angel" to "😇", "cool" to "😎",
        "nerd" to "🤓", "think" to "🤔", "shush" to "🤫", "sleep" to "😴",
        "sick" to "🤢", "angry" to "😡", "sad" to "😢", "happy" to "😊",
        "wink" to "😉", "kiss" to "😘", "tongue" to "😛", "shock" to "😱",
        "100" to "💯", "sparkle" to "✨", "rainbow" to "🌈", "lightning" to "⚡",
        "earth" to "🌍", "world" to "🌍", "peace" to "☮️", "yin" to "☯️",
        "up" to "⬆️", "down" to "⬇️", "left" to "⬅️", "right" to "➡️",
        "plus" to "➕", "minus" to "➖", "question" to "❓", "exclaim" to "❗",
        "idea" to "💡", "bulb" to "💡", "pin" to "📌", "pencil" to "✏️",
        "book" to "📖", "calendar" to "📅", "chart" to "📊", "target" to "🎯",
        "hand" to "✋", "point" to "👆", "fist" to "✊", "muscle" to "💪",
        "eye" to "👁️", "brain" to "🧠", "baby" to "👶", "family" to "👨‍👩‍👧‍👦",
        "shirt" to "👕", "shoe" to "👟", "hat" to "🎩", "glasses" to "👓",
        "soccer" to "⚽", "basket" to "🏀", "tennis" to "🎾", "dice" to "🎲"
    )

    fun search(query: String): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase().trim()
        val results = mutableListOf<SearchResult>()

        // Search emojis
        emojiSearch.entries
            .filter { it.key.contains(q) }
            .take(8)
            .forEach { (name, emoji) ->
                results.add(SearchResult.EmojiResult(emoji, name))
            }

        // Search kaomoji
        KaomojiData.categories.forEach { cat ->
            cat.items.filter { it.lowercase().contains(q) }
                .take(3)
                .forEach { kaomoji ->
                    results.add(SearchResult.KaomojiResult(kaomoji, cat.name))
                }
        }
        // Also search by category name
        KaomojiData.categories
            .filter { it.name.lowercase().contains(q) }
            .take(2)
            .forEach { cat ->
                cat.items.take(3).forEach { kaomoji ->
                    results.add(SearchResult.KaomojiResult(kaomoji, cat.name))
                }
            }

        // Search clipboard
        clipboardHistory?.items
            ?.filter { it.text?.lowercase()?.contains(q) == true }
            ?.take(3)
            ?.forEach { item ->
                item.text?.let { results.add(SearchResult.ClipboardResult(it)) }
            }

        return results.take(15)
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
    val focusRequester = remember { FocusRequester() }
    val panelHeight = (220 * heightScale).dp

    LaunchedEffect(query) {
        results.clear()
        if (query.isNotBlank()) {
            results.addAll(engine.search(query))
        }
    }

    LaunchedEffect(Unit) {
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KeyBackground)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "🔍  Search emoji, kaomoji, clipboard...",
                        color = KeyTextDim,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(
                        color = KeyText,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(ShiftActiveBackground),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            Spacer(Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ActionKeyBackground)
                    .clickable { onClose() }
                    .semantics {
                        contentDescription = "Close search"
                        role = Role.Button
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "✕", color = KeyText, fontSize = 16.sp)
            }
        }

        // Results
        if (results.isEmpty() && query.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(panelHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No results for \"$query\"",
                    color = KeyTextDim,
                    fontSize = 14.sp
                )
            }
        } else if (results.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(panelHeight),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(results) { result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(KeyBackground)
                            .clickable { onResultClick(result.value) }
                            .semantics {
                                contentDescription = "Insert ${result.displayText}"
                                role = Role.Button
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = result.displayText,
                            color = KeyText,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = result.category,
                            color = KeyTextDim,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            // Empty state — show popular
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(panelHeight),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 32.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Search emoji, kaomoji, or clipboard",
                        color = KeyTextDim,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Try: smile, heart, dog, fire, party...",
                        color = KeyTextDim.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
