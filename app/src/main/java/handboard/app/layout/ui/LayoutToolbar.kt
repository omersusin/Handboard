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

enum class KeyboardPanel { KEYBOARD, EMOJI, CLIPBOARD, KAOMOJI, TEXT_EDITING, SEARCH, TRANSLATE, CURRENCY, PHRASES }

@Composable
fun LayoutToolbar(
    currentLayoutName: String, currentPanel: KeyboardPanel,
    searchEnabled: Boolean, currencyEnabled: Boolean, clipboardEnabled: Boolean, 
    kaomojiEnabled: Boolean, phrasesEnabled: Boolean, translateEnabled: Boolean, 
    textEditingEnabled: Boolean, emojiEnabled: Boolean,
    onSwitchLayout: () -> Unit, onSwitchPanel: (KeyboardPanel) -> Unit, onOpenSettings: () -> Unit
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
                KeyboardPanel.SEARCH -> "Search"; KeyboardPanel.TRANSLATE -> "Translate"; 
                KeyboardPanel.CURRENCY -> "Currency"; KeyboardPanel.PHRASES -> "Phrases"
            }, color = KeyTextDim, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (currentPanel != KeyboardPanel.KEYBOARD) ToolbarIconButton({ KeyboardIcon(tint = KeyText, size = 16.dp) }, false) { onSwitchPanel(KeyboardPanel.KEYBOARD) }
            if (phrasesEnabled) ToolbarIconButton({ PhraseIcon(tint = KeyText, size = 16.dp) }, currentPanel == KeyboardPanel.PHRASES) { onSwitchPanel(if (currentPanel == KeyboardPanel.PHRASES) KeyboardPanel.KEYBOARD else KeyboardPanel.PHRASES) }
            if (currencyEnabled) ToolbarIconButton({ CurrencyIcon(tint = KeyText, size = 16.dp) }, currentPanel == KeyboardPanel.CURRENCY) { onSwitchPanel(if (currentPanel == KeyboardPanel.CURRENCY) KeyboardPanel.KEYBOARD else KeyboardPanel.CURRENCY) }
            if (translateEnabled) ToolbarIconButton({ TranslateIcon(tint = KeyText, size = 16.dp) }, currentPanel == KeyboardPanel.TRANSLATE) { onSwitchPanel(if (currentPanel == KeyboardPanel.TRANSLATE) KeyboardPanel.KEYBOARD else KeyboardPanel.TRANSLATE) }
            if (searchEnabled) ToolbarIconButton({ SearchIcon(tint=KeyText, size=16.dp) }, currentPanel == KeyboardPanel.SEARCH) { onSwitchPanel(if (currentPanel == KeyboardPanel.SEARCH) KeyboardPanel.KEYBOARD else KeyboardPanel.SEARCH) }
            if (textEditingEnabled) ToolbarIconButton({ EditIcon(tint=KeyText, size=16.dp) }, currentPanel == KeyboardPanel.TEXT_EDITING) { onSwitchPanel(if (currentPanel == KeyboardPanel.TEXT_EDITING) KeyboardPanel.KEYBOARD else KeyboardPanel.TEXT_EDITING) }
            if (clipboardEnabled) ToolbarIconButton({ ClipboardIcon(tint=KeyText, size=16.dp) }, currentPanel == KeyboardPanel.CLIPBOARD) { onSwitchPanel(if (currentPanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.KEYBOARD else KeyboardPanel.CLIPBOARD) }
            if (kaomojiEnabled) ToolbarIconButton({ KaomojiIcon(tint = KeyText, size = 16.dp) }, currentPanel == KeyboardPanel.KAOMOJI) { onSwitchPanel(if (currentPanel == KeyboardPanel.KAOMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.KAOMOJI) }
            if (emojiEnabled) ToolbarIconButton({ EmojiIcon(tint=KeyText, size=16.dp) }, currentPanel == KeyboardPanel.EMOJI) { onSwitchPanel(if (currentPanel == KeyboardPanel.EMOJI) KeyboardPanel.KEYBOARD else KeyboardPanel.EMOJI) }
            ToolbarIconButton({ GlobeIcon(tint=KeyText, size=16.dp) }, false, onSwitchLayout)
            ToolbarIconButton({ SettingsGearIcon(modifier=Modifier.size(16.dp), tint=KeyText) }, false, onOpenSettings)
        }
    }
}

@Composable
private fun ToolbarIconButton(content: @Composable () -> Unit, isActive: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(if (isActive) ShiftActiveBackground else ActionKeyBackground).clickable(onClick = onClick), contentAlignment = Alignment.Center) { content() }
}
