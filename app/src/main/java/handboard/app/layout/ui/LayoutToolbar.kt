package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.ShiftActiveBackground

enum class KeyboardPanel {
    KEYBOARD, EMOJI, CLIPBOARD
}

@Composable
fun LayoutToolbar(
    currentLayoutName: String,
    currentPanel: KeyboardPanel,
    onSwitchLayout: () -> Unit,
    onSwitchPanel: (KeyboardPanel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: layout name or panel name
        Text(
            text = when (currentPanel) {
                KeyboardPanel.KEYBOARD -> currentLayoutName
                KeyboardPanel.EMOJI -> "Emoji"
                KeyboardPanel.CLIPBOARD -> "Clipboard"
            },
            color = KeyTextDim,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // ABC button (when not on keyboard)
            if (currentPanel != KeyboardPanel.KEYBOARD) {
                ToolbarButton(
                    content = { Text("ABC", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    isActive = false,
                    onClick = { onSwitchPanel(KeyboardPanel.KEYBOARD) }
                )
                Spacer(Modifier.width(4.dp))
            }

            // Clipboard button
            ToolbarButton(
                content = { ClipboardIcon(tint = KeyText, size = 18.dp) },
                isActive = currentPanel == KeyboardPanel.CLIPBOARD,
                onClick = {
                    onSwitchPanel(
                        if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD
                        else KeyboardPanel.CLIPBOARD
                    )
                }
            )

            Spacer(Modifier.width(4.dp))

            // Emoji button
            ToolbarButton(
                content = { Text("😊", fontSize = 16.sp) },
                isActive = currentPanel == KeyboardPanel.EMOJI,
                onClick = {
                    onSwitchPanel(
                        if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD
                        else KeyboardPanel.EMOJI
                    )
                }
            )

            Spacer(Modifier.width(4.dp))

            // Layout switch button
            ToolbarButton(
                content = { GlobeIcon(tint = KeyText, size = 18.dp) },
                isActive = false,
                onClick = onSwitchLayout
            )
        }
    }
}

@Composable
private fun ToolbarButton(
    content: @Composable () -> Unit,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) ShiftActiveBackground else ActionKeyBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
