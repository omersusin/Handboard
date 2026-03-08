package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.ShiftActiveBackground
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyStyle

@Composable
fun RowScope.KeyView(
    keyData: KeyData,
    isShifted: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        keyData.action is KeyAction.Shift && isShifted -> ShiftActiveBackground
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val textColor = when (keyData.style) {
        KeyStyle.SPECIAL -> KeyTextDim
        else -> KeyText
    }

    val displayLabel = when {
        keyData.action is KeyAction.Text && isShifted ->
            keyData.label.uppercase()
        else -> keyData.label
    }

    val fontSize = when {
        keyData.action is KeyAction.Space -> 13.sp
        keyData.style == KeyStyle.SPECIAL -> 13.sp
        keyData.style == KeyStyle.ACTION -> 18.sp
        else -> 18.sp
    }

    Box(
        modifier = Modifier
            .weight(keyData.widthWeight)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayLabel,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (keyData.style == KeyStyle.NORMAL) FontWeight.Normal else FontWeight.Medium
        )
    }
}
