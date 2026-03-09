package handboard.app.search

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground
import handboard.app.layout.ui.*
import java.net.URLEncoder

@Composable
fun SearchPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onTextCommit: (String) -> Unit,
    onClose: () -> Unit,
    maxHeight: Int = 260
) {
    val repository = remember { SearchRepository() }
    val suggestions by repository.suggestions.collectAsState()
    val isLoading by repository.isLoading.collectAsState()
    var browserUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(query) { repository.onQueryChanged(query) }
    DisposableEffect(Unit) { onDispose { repository.destroy() } }

    fun openSearch(q: String) {
        browserUrl = "https://www.google.com/search?q=${URLEncoder.encode(q.trim(), "UTF-8")}"
    }

    AnimatedContent(targetState = browserUrl, label = "search_screen") { url ->
        if (url != null) {
            InKeyboardBrowser(
                url = url,
                onClose = { browserUrl = null },
                onCommitText = { onTextCommit(it); onClose() },
                modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight.dp)
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight.dp).background(KeyboardBackground).padding(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(KeyBackground).clickable { openSearch(query) }.padding(10.dp)) {
                        if (query.isEmpty()) Text("🔍 Google Search...", color = KeyTextDim, fontSize = 14.sp)
                        else Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(query, color = KeyText, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Box(modifier = Modifier.padding(start = 2.dp).width(2.dp).height(16.dp).background(ShiftActiveBackground))
                        }
                    }
                    Spacer(Modifier.width(6.dp))
                    if (query.isNotBlank()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ShiftActiveBackground).clickable { openSearch(query) }.padding(10.dp)) {
                            Text("Go", color = KeyText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(6.dp))
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(10.dp)) {
                        Text("✕", color = KeyText)
                    }
                }

                Spacer(Modifier.height(4.dp))
                if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(2.dp), color = ShiftActiveBackground)
                
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(suggestions) { suggestion ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { openSearch(suggestion) }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SearchIcon(tint = KeyTextDim, size = 16.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(text = suggestion, color = KeyText, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f), fontWeight = if (suggestion.startsWith(query, true)) FontWeight.Normal else FontWeight.Bold)
                            TravelExploreIcon(tint = ShiftActiveBackground, size = 16.dp)
                        }
                    }
                }
            }
        }
    }
}
