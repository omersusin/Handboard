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

enum class KeyboardPanel { KEYBOARD, EMOJI, CLIPBOARD, KAOMOJI, TEXT_EDITING, SEARCH, TRANSLATE, CURRENCY }

@Composable
fun LayoutToolbar(
    currentLayoutName: String, currentPanel: KeyboardPanel, clipboardEnabled: Boolean,
    onSwitchLayout: () -> Unit, onSwitchPanel: (KeyboardPanel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(38.dp).padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = when (currentPanel) {
                KeyboardPanel.KEYBOARD -> currentLayoutName
                KeyboardPanel.EMOJI -> "Emoji"; KeyboardPanel.CLIPBOARD -> "Clipboard"
                KeyboardPanel.KAOMOJI -> "Kaomoji"; KeyboardPanel.TEXT_EDITING -> "Edit"
                KeyboardPanel.SEARCH -> "Search"; KeyboardPanel.TRANSLATE -> "Translate"; KeyboardPanel.CURRENCY -> "Currency"
            }, color = KeyTextDim, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            
            // 1. Keyboard Switch (if in a panel)
            if (currentPanel != KeyboardPanel.KEYBOARD) {
                ToolbarIconButton({ KeyboardIcon(tint = KeyText, size = 16.dp) }, "Keyboard", false) { onSwitchPanel(KeyboardPanel.KEYBOARD) }
            }
            
            // 2. Currency
            ToolbarIconButton({ CurrencyIcon(tint = KeyText, size = 16.dp) }, "Currency", currentPanel == KeyboardPanel.CURRENCY) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.CURRENCY) KeyboardPanel.KEYBOARD else KeyboardPanel.CURRENCY) 
            }
            
            // 3. Translate
            ToolbarIconButton({ TranslateIcon(tint = KeyText, size = 16.dp) }, "Translate", currentPanel == KeyboardPanel.TRANSLATE) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.TRANSLATE) KeyboardPanel.KEYBOARD else KeyboardPanel.TRANSLATE) 
            }
            
            // 4. Search
            ToolbarIconButton({ SearchIcon(tint = KeyText, size = 16.dp) }, "Search", currentPanel == KeyboardPanel.SEARCH) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.SEARCH) KeyboardPanel.KEYBOARD else KeyboardPanel.SEARCH) 
            }
            
            // 5. Edit
            ToolbarIconButton({ EditIcon(tint = KeyText, size = 16.dp) }, "Text editing", currentPanel == KeyboardPanel.TEXT_EDITING) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.TEXT_EDITING) KeyboardPanel.KEYBOARD else KeyboardPanel.TEXT_EDITING) 
            }
            
            // 6. Clipboard
            if (clipboardEnabled) {
                ToolbarIconButton({ ClipboardIcon(tint = KeyText, size = 16.dp) }, "Clipboard", currentPanel == KeyboardPanel.CLIPBOARD) { 
                    onSwitchPanel(if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD else KeyboardPanel.CLIPBOARD) 
                }
            }
            
            // 7. Kaomoji
            ToolbarIconButton({ KaomojiIcon(tint = KeyText, size = 16.dp) }, "Kaomoji", currentPanel == KeyboardPanel.KAOMOJI) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.KAOMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.KAOMOJI) 
            }
            
            // 8. Emoji
            ToolbarIconButton({ EmojiIcon(tint = KeyText, size = 16.dp) }, "Emoji", currentPanel == KeyboardPanel.EMOJI) { 
                onSwitchPanel(if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.EMOJI) 
            }
            
            // 9. Globe (Layout Switcher)
            ToolbarIconButton({ GlobeIcon(tint = KeyText, size = 16.dp) }, "Switch layout", false, onSwitchLayout)
        }
    }
}

@Composable
private fun ToolbarIconButton(content: @Composable () -> Unit, description: String, isActive: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(if (isActive) ShiftActiveBackground else ActionKeyBackground).clickable(onClick = onClick).semantics { contentDescription = description; role = Role.Button }, contentAlignment = Alignment.Center) { content() }
}
