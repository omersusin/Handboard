package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.*

@Composable
fun PhrasesPanel(
    onCommitText: (String) -> Unit,
    onClose: () -> Unit,
    maxHeight: Int = 260
) {
    val phrases = listOf(
        "Merhaba!", "Nasılsın?", "Geliyorum.", "Sonra arayacağım.",
        "Evet", "Hayır", "Teşekkürler", "Tamam",
        "Ne yapıyorsun?", "Haber ver.", "Haklısın.", "Görüşürüz."
    )

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight.dp).background(KeyboardBackground).padding(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("💬 Quick Phrases", color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(8.dp)) {
                Text("✕", color = KeyText, fontSize = 12.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(phrases) { phrase ->
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(KeyBackground).clickable { onCommitText(phrase) }.padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = phrase, color = KeyText, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
