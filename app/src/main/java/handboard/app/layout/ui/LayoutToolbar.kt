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
    KEYBOARD, EMOJI, CLIPBOARD, KAOMOJI, TEXT_EDITING
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
            .padding(horizontal = 4.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: panel name
        Text(
            text = when (currentPanel) {
                KeyboardPanel.KEYBOARD -> currentLayoutName
                KeyboardPanel.EMOJI -> "Emoji"
                KeyboardPanel.CLIPBOARD -> "Clipboard"
                KeyboardPanel.KAOMOJI -> "Kaomoji"
                KeyboardPanel.TEXT_EDITING -> "Editing"
            },
            color = KeyTextDim,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 6.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            // ABC button (when not on keyboard)
            if (currentPanel != KeyboardPanel.KEYBOARD) {
                ToolbarBtn("ABC", "Switch to keyboard", currentPanel == KeyboardPanel.KEYBOARD) {
                    onSwitchPanel(KeyboardPanel.KEYBOARD)
                }
                Spacer(Modifier.width(3.dp))
            }

            // Text editing
            ToolbarBtn("✎", "Text editing", currentPanel == KeyboardPanel.TEXT_EDITING) {
                onSwitchPanel(
                    if (currentPanel == KeyboardPanel.TEXT_EDITING) KeyboardPanel.KEYBOARD
                    else KeyboardPanel.TEXT_EDITING
                )
            }
            Spacer(Modifier.width(3.dp))

            // Clipboard (only if enabled)
            if (clipboardEnabled) {
                ToolbarBtn(content = { ClipboardIcon(tint = KeyText, size = 16.dp) },
                    desc = "Clipboard",
                    active = currentPanel == KeyboardPanel.CLIPBOARD) {
                    onSwitchPanel(
                        if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD
                        else KeyboardPanel.CLIPBOARD
                    )
                }
                Spacer(Modifier.width(3.dp))
            }

            // Kaomoji
            ToolbarBtn("(◕‿◕)", "Kaomoji", currentPanel == KeyboardPanel.KAOMOJI) {
                onSwitchPanel(
                    if (currentPanel == KeyboardPanel.KAOMOJI) KeyboardPanel.KEYBOARD
                    else KeyboardPanel.KAOMOJI
                )
            }
            Spacer(Modifier.width(3.dp))

            // Emoji
            ToolbarBtn("😊", "Emoji", currentPanel == KeyboardPanel.EMOJI) {
                onSwitchPanel(
                    if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD
                    else KeyboardPanel.EMOJI
                )
            }
            Spacer(Modifier.width(3.dp))

            // Layout switch
            ToolbarBtn(content = { GlobeIcon(tint = KeyText, size = 16.dp) },
                desc = "Switch layout", active = false, onClick = onSwitchLayout)
        }
    }
}

@Composable
private fun ToolbarBtn(
    label: String,
    desc: String,
    active: Boolean,
    onClick: () -> Unit
) {
    ToolbarBtn(content = {
        Text(label, color = KeyText, fontSize = if (label.length > 3) 10.sp else 12.sp,
            fontWeight = FontWeight.Medium, maxLines = 1)
    }, desc = desc, active = active, onClick = onClick)
}

@Composable
private fun ToolbarBtn(
    content: @Composable () -> Unit,
    desc: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (active) ShiftActiveBackground else ActionKeyBackground)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = desc
                role = Role.Button
            }
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
