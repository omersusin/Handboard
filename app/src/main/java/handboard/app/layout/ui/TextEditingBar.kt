package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground

data class EditAction(
    val label: String,
    val icon: String,
    val description: String,
    val action: () -> Unit
)

@Composable
fun TextEditingBar(
    onCursorLeft: () -> Unit,
    onCursorRight: () -> Unit,
    onCursorHome: () -> Unit,
    onCursorEnd: () -> Unit,
    onSelectAll: () -> Unit,
    onCopy: () -> Unit,
    onCut: () -> Unit,
    onPaste: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClose: () -> Unit
) {
    val row1 = listOf(
        EditAction("◀", "◀", "Cursor left", onCursorLeft),
        EditAction("▶", "▶", "Cursor right", onCursorRight),
        EditAction("⇤", "⇤", "Cursor to start", onCursorHome),
        EditAction("⇥", "⇥", "Cursor to end", onCursorEnd),
        EditAction("Select All", "☐", "Select all text", onSelectAll)
    )

    val row2 = listOf(
        EditAction("Copy", "📋", "Copy", onCopy),
        EditAction("Cut", "✂", "Cut", onCut),
        EditAction("Paste", "📌", "Paste", onPaste),
        EditAction("Undo", "↩", "Undo", onUndo),
        EditAction("Redo", "↪", "Redo", onRedo)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: Cursor controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row1.forEach { action ->
                EditButton(
                    label = action.icon,
                    description = action.description,
                    modifier = Modifier.weight(1f),
                    onClick = action.action
                )
            }
        }

        // Row 2: Clipboard + undo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row2.forEach { action ->
                EditButton(
                    label = action.icon,
                    description = action.description,
                    modifier = Modifier.weight(1f),
                    onClick = action.action
                )
            }
        }

        // Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ShiftActiveBackground)
                    .clickable { onClose() }
                    .semantics {
                        contentDescription = "Close text editing"
                        role = Role.Button
                    }
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ABC",
                    color = KeyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EditButton(
    label: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KeyBackground)
            .clickable { onClick() }
            .semantics {
                contentDescription = description
                role = Role.Button
            }
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = KeyText,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
