package handboard.app.layout.ui

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
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
import handboard.app.translate.TranslationEngine
import handboard.app.translate.TranslationLanguages
import kotlinx.coroutines.delay

@Composable
fun TranslatePanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var sourceLang by remember { mutableStateOf("auto") }
    var targetLang by remember { mutableStateOf("en") }
    var translatedText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSrcMenu by remember { mutableStateOf(false) }
    var showTrgMenu by remember { mutableStateOf(false) }

    LaunchedEffect(query, sourceLang, targetLang) {
        if (query.isBlank()) {
            translatedText = ""
            return@LaunchedEffect
        }
        isLoading = true
        delay(500)
        if (sourceLang == targetLang && sourceLang != "auto") {
            translatedText = query
            isLoading = false
            return@LaunchedEffect
        }
        TranslationEngine.translate(sourceLang, targetLang, query).fold(
            onSuccess = { translatedText = it; isLoading = false },
            onFailure = { translatedText = "Error"; isLoading = false }
        )
    }

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(KeyboardBackground)) {
        
        // Top Bar: Lang Selection & Swap
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LangBtn(label = TranslationLanguages.items[sourceLang] ?: sourceLang, onClick = { showSrcMenu = true }, modifier = Modifier.weight(1f))
            
            Box(modifier = Modifier.padding(horizontal = 4.dp).clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable {
                if (sourceLang != "auto") { val tmp = sourceLang; sourceLang = targetLang; targetLang = tmp }
            }.padding(8.dp)) {
                Text("⇄", color = KeyText)
            }
            
            LangBtn(label = TranslationLanguages.items[targetLang] ?: targetLang, onClick = { showTrgMenu = true }, modifier = Modifier.weight(1f))
            
            Spacer(Modifier.width(4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(8.dp)) {
                Text("✕", color = KeyText)
            }
        }

        // Language List Overlay
        if (showSrcMenu || showTrgMenu) {
            val isSource = showSrcMenu
            val mapItems = if (isSource) TranslationLanguages.items else TranslationLanguages.items.filterKeys { it != "auto" }
            val listItems = mapItems.entries.map { Pair(it.key, it.value) }
            
            LazyColumn(modifier = Modifier.fillMaxWidth().height(140.dp).background(KeyBackground)) {
                items(listItems) { pair ->
                    Text(
                        text = pair.second, color = KeyText, fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (isSource) sourceLang = pair.first else targetLang = pair.first
                            showSrcMenu = false; showTrgMenu = false
                        }.padding(12.dp)
                    )
                }
            }
        } else {
            // Input & Output Area
            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                // Input
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(KeyBackground).padding(10.dp)) {
                    if (query.isEmpty()) Text("Type to translate...", color = KeyTextDim, fontSize = 14.sp)
                    else {
                        Row {
                            Text(query, color = KeyText, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Box(modifier = Modifier.padding(start = 2.dp).width(2.dp).height(16.dp).background(ShiftActiveBackground))
                        }
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                // Output
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = ShiftActiveBackground, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(translatedText.ifEmpty { "Translation..." }, color = if (translatedText.isEmpty()) KeyTextDim else KeyText, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    
                    if (translatedText.isNotEmpty() && !isLoading) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ShiftActiveBackground).clickable { onInsertText(translatedText); onQueryChange("") }.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text("Insert", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LangBtn(label: String, onClick: () -> Unit, modifier: Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable(onClick = onClick).padding(8.dp), contentAlignment = Alignment.Center) {
        Text(label, color = KeyTextDim, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
