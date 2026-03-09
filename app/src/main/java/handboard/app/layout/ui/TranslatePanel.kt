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
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var sourceText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var sourceLang by remember { mutableStateOf("auto") }
    var targetLang by remember { mutableStateOf("tr") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var showSrcMenu by remember { mutableStateOf(false) }
    var showTrgMenu by remember { mutableStateOf(false) }

    LaunchedEffect(sourceText, sourceLang, targetLang) {
        if (sourceText.isBlank()) {
            translatedText = ""
            error = null
            return@LaunchedEffect
        }
        isLoading = true
        error = null
        delay(600)

        if (sourceLang == targetLang && sourceLang != "auto") {
            translatedText = sourceText
            isLoading = false
            return@LaunchedEffect
        }

        TranslationEngine.translate(sourceLang, targetLang, sourceText).fold(
            onSuccess = { translatedText = it; isLoading = false },
            onFailure = { error = it.message; isLoading = false }
        )
    }

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(KeyboardBackground).padding(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LangSelector(label = TranslationLanguages.items[sourceLang] ?: sourceLang, onClick = { showSrcMenu = !showSrcMenu }, modifier = Modifier.weight(1f))
            Text(" ➔ ", color = KeyTextDim, modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterVertically))
            LangSelector(label = TranslationLanguages.items[targetLang] ?: targetLang, onClick = { showTrgMenu = !showTrgMenu }, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(10.dp)) {
                Text("✕", color = KeyText)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (showSrcMenu) {
            LangList(isSource = true, onSelect = { sourceLang = it; showSrcMenu = false })
        } else if (showTrgMenu) {
            LangList(isSource = false, onSelect = { targetLang = it; showTrgMenu = false })
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(KeyBackground).padding(8.dp)) {
                if (sourceText.isEmpty()) Text("Type to translate...", color = KeyTextDim, fontSize = 14.sp)
                BasicTextField(
                    value = sourceText, onValueChange = { sourceText = it },
                    textStyle = TextStyle(color = KeyText, fontSize = 14.sp),
                    cursorBrush = SolidColor(ShiftActiveBackground), modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).padding(8.dp)) {
                if (isLoading) {
                    CircularProgressIndicator(color = ShiftActiveBackground, modifier = Modifier.align(Alignment.Center).padding(4.dp))
                } else if (error != null) {
                    Text("Error: $error", color = handboard.app.core.theme.Error80, fontSize = 12.sp)
                } else {
                    Text(translatedText.ifEmpty { "Translation will appear here" }, color = if (translatedText.isEmpty()) KeyTextDim else KeyText, fontSize = 14.sp)
                }

                if (translatedText.isNotEmpty() && !isLoading) {
                    Box(modifier = Modifier.align(Alignment.BottomEnd).clip(RoundedCornerShape(6.dp)).background(ShiftActiveBackground).clickable { onInsertText(translatedText); sourceText="" }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("Insert", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LangSelector(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable(onClick = onClick).padding(10.dp), contentAlignment = Alignment.Center) {
        Text(label, color = KeyText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LangList(isSource: Boolean, onSelect: (String) -> Unit) {
    // Convert Map.Entry to a Pair explicitly to avoid component() ambiguous errors
    val mapItems = if (isSource) TranslationLanguages.items else TranslationLanguages.items.filterKeys { it != "auto" }
    val listItems = mapItems.entries.map { Pair(it.key, it.value) }
    
    LazyColumn(modifier = Modifier.fillMaxWidth().height(128.dp)) {
        items(listItems) { pair ->
            Box(modifier = Modifier.fillMaxWidth().clickable { onSelect(pair.first) }.padding(12.dp)) {
                Text(pair.second, color = KeyText, fontSize = 14.sp)
            }
        }
    }
}
