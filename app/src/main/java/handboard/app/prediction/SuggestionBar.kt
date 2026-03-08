package handboard.app.prediction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground

@Composable
fun SuggestionBar(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) {
        // Empty bar — fixed height so layout doesn't jump
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(KeyboardBackground)
        )
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(KeyboardBackground)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        suggestions.forEachIndexed { index, word ->
            val isFirst = index == 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isFirst) ActionKeyBackground else ActionKeyBackground.copy(alpha = 0.5f))
                    .clickable { onSuggestionClick(word) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word,
                    color = if (isFirst) KeyText else KeyTextDim,
                    fontSize = 14.sp,
                    fontWeight = if (isFirst) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
