package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    KEYBOARD, EMOJI, CLIPBOARD, KAOMOJI, TEXT_EDITING, SEARCH
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
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: layout/panel label
        Text(
            text = when (currentPanel) {
                KeyboardPanel.KEYBOARD -> currentLayoutName
                KeyboardPanel.EMOJI -> "Emoji"
                KeyboardPanel.CLIPBOARD -> "Clipboard"
                KeyboardPanel.KAOMOJI -> "Kaomoji"
                KeyboardPanel.TEXT_EDITING -> "Edit"
                KeyboardPanel.SEARCH -> "Search"
            },
            color = KeyTextDim,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Right: action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ABC (only when not on keyboard)
            if (currentPanel != KeyboardPanel.KEYBOARD) {
                ToolbarIconButton(
                    content = { Text("ABC", color = KeyText, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    description = "Keyboard",
                    isActive = false,
                    onClick = { onSwitchPanel(KeyboardPanel.KEYBOARD) }
                )
            }

            // Search
            ToolbarIconButton(
                content = { SearchIcon(tint = KeyText, size = 16.dp) },
                description = "Search",
                isActive = currentPanel == KeyboardPanel.SEARCH,
                onClick = { onSwitchPanel(if (currentPanel == KeyboardPanel.SEARCH) KeyboardPanel.KEYBOARD else KeyboardPanel.SEARCH) }
            )

            // Edit
            ToolbarIconButton(
                content = { EditIcon(tint = KeyText, size = 16.dp) },
                description = "Text editing",
                isActive = currentPanel == KeyboardPanel.TEXT_EDITING,
                onClick = { onSwitchPanel(if (currentPanel == KeyboardPanel.TEXT_EDITING) KeyboardPanel.KEYBOARD else KeyboardPanel.TEXT_EDITING) }
            )

            // Clipboard (if enabled)
            if (clipboardEnabled) {
                ToolbarIconButton(
                    content = { ClipboardIcon(tint = KeyText, size = 16.dp) },
                    description = "Clipboard",
                    isActive = currentPanel == KeyboardPanel.CLIPBOARD,
                    onClick = { onSwitchPanel(if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD else KeyboardPanel.CLIPBOARD) }
                )
            }

            // Kaomoji
            ToolbarIconButton(
                content = { KaomojiIcon(tint = KeyText, size = 16.dp) },
                description = "Kaomoji",
                isActive = currentPanel == KeyboardPanel.KAOMOJI,
                onClick = { onSwitchPanel(if (currentPanel == KeyboardPanel.KAOMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.KAOMOJI) }
            )

            // Emoji
            ToolbarIconButton(
                content = { EmojiIcon(tint = KeyText, size = 16.dp) },
                description = "Emoji",
                isActive = currentPanel == KeyboardPanel.EMOJI,
                onClick = { onSwitchPanel(if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.EMOJI) }
            )

            // Globe / Layout switch
            ToolbarIconButton(
                content = { GlobeIcon(tint = KeyText, size = 16.dp) },
                description = "Switch layout",
                isActive = false,
                onClick = onSwitchLayout
            )
        }
    }
}

@Composable
private fun ToolbarIconButton(
    content: @Composable () -> Unit,
    description: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) ShiftActiveBackground else ActionKeyBackground)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
