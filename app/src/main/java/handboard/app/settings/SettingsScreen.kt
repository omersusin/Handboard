package handboard.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import handboard.app.layout.LayoutRegistry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val heightScale by preferencesManager.keyboardHeight.collectAsState(initial = 1.0f)
    val widthPercent by preferencesManager.keyboardWidth.collectAsState(initial = 100)
    val alignment by preferencesManager.keyboardAlignment.collectAsState(initial = 1)
    val layoutName by preferencesManager.selectedLayout.collectAsState(initial = "QWERTY")
    val haptic by preferencesManager.hapticEnabled.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Layout Selection
            SectionCard(title = "Layout") {
                val allLayouts = LayoutRegistry.getAllNames()
                allLayouts.forEach { name ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { scope.launch { preferencesManager.setSelectedLayout(name) } }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = name == layoutName,
                            onClick = { scope.launch { preferencesManager.setSelectedLayout(name) } }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // Keyboard Size
            SectionCard(title = "Size") {
                Text(
                    text = "Height: ${"%.1f".format(heightScale)}x",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = heightScale,
                    onValueChange = { scope.launch { preferencesManager.setKeyboardHeight(it) } },
                    valueRange = 0.7f..1.5f,
                    steps = 7
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Width: ${widthPercent}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = widthPercent.toFloat(),
                    onValueChange = { scope.launch { preferencesManager.setKeyboardWidth(it.toInt()) } },
                    valueRange = 50f..100f,
                    steps = 9
                )
            }

            // Keyboard Position
            SectionCard(title = "Position") {
                val options = listOf("Left", "Center", "Right")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    options.forEachIndexed { index, label ->
                        FilterChip(
                            selected = index == alignment,
                            onClick = { scope.launch { preferencesManager.setKeyboardAlignment(index) } },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Feedback
            SectionCard(title = "Feedback") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Feedback", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = haptic,
                        onCheckedChange = { scope.launch { preferencesManager.setHapticEnabled(it) } }
                    )
                }
            }

            // About
            SectionCard(title = "About") {
                Text("HandBoard v1.0.0", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "One-handed keyboard for everyone",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
