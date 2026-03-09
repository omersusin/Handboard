package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.ShiftActiveBackground

enum class KeyboardPanel {
    KEYBOARD, EMOJI, CLIPBOARD, KAOMOJI, TEXT_EDITING, SEARCH, TRANSLATE
}

@Composable
fun LayoutToolbar(
    currentLayoutName: String,
    currentPanel: KeyboardPanel,
    clipboardEnabled: Boolean,
    onSwitchLayout: () -> Unit,
    onSwitchPanel: (KeyboardPanel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(38.dp).padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (currentPanel) {
                KeyboardPanel.KEYBOARD -> currentLayoutName
                KeyboardPanel.EMOJI -> "Emoji"
                KeyboardPanel.CLIPBOARD -> "Clipboard"
                KeyboardPanel.KAOMOJI -> "Kaomoji"
                KeyboardPanel.TEXT_EDITING -> "Edit"
                KeyboardPanel.SEARCH -> "Search"
                KeyboardPanel.TRANSLATE -> "Translate"
            },
            color = KeyTextDim, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (currentPanel != KeyboardPanel.KEYBOARD) {
                ToolbarIconButton({ Text("ABC", color = KeyText, fontSize = 11.sp, fontWeight = FontWeight.Bold) }, "Keyboard", false) { onSwitchPanel(KeyboardPanel.KEYBOARD) }
            }
            
            ToolbarIconButton({ Text("A文", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.Bold) }, "Translate", currentPanel == KeyboardPanel.TRANSLATE) {
                onSwitchPanel(if (currentPanel == KeyboardPanel.TRANSLATE) KeyboardPanel.KEYBOARD else KeyboardPanel.TRANSLATE)
            }

            ToolbarIconButton({ SearchIcon(tint = KeyText, size = 16.dp) }, "Search", currentPanel == KeyboardPanel.SEARCH) {
                onSwitchPanel(if (currentPanel == KeyboardPanel.SEARCH) KeyboardPanel.KEYBOARD else KeyboardPanel.SEARCH)
            }

            ToolbarIconButton({ EditIcon(tint = KeyText, size = 16.dp) }, "Text editing", currentPanel == KeyboardPanel.TEXT_EDITING) {
                onSwitchPanel(if (currentPanel == KeyboardPanel.TEXT_EDITING) KeyboardPanel.KEYBOARD else KeyboardPanel.TEXT_EDITING)
            }

            if (clipboardEnabled) {
                ToolbarIconButton({ ClipboardIcon(tint = KeyText, size = 16.dp) }, "Clipboard", currentPanel == KeyboardPanel.CLIPBOARD) {
                    onSwitchPanel(if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD else KeyboardPanel.CLIPBOARD)
                }
            }

            ToolbarIconButton({ EmojiIcon(tint = KeyText, size = 16.dp) }, "Emoji", currentPanel == KeyboardPanel.EMOJI) {
                onSwitchPanel(if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.EMOJI)
            }

            ToolbarIconButton({ GlobeIcon(tint = KeyText, size = 16.dp) }, "Switch layout", false, onSwitchLayout)
        }
    }
}

@Composable
private fun ToolbarIconButton(content: @Composable () -> Unit, description: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(if (isActive) ShiftActiveBackground else ActionKeyBackground).clickable(onClick = onClick)
            .semantics { contentDescription = description; role = Role.Button },
        contentAlignment = Alignment.Center
    ) { content() }
}
