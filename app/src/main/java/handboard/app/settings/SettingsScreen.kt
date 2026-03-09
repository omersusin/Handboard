package handboard.app.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import handboard.app.R
import handboard.app.layout.LayoutRegistry
import handboard.app.layout.ui.BackArrowIcon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    val suggestionCount by preferencesManager.suggestionCount.collectAsState(initial = 3)
    val predictionsEnabled by preferencesManager.predictionsEnabled.collectAsState(initial = true)
    val bottomPadding by preferencesManager.bottomPadding.collectAsState(initial = 0)
    val clipboardEnabled by preferencesManager.clipboardEnabled.collectAsState(initial = false)
    val followSystemTheme by preferencesManager.followSystemTheme.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackArrowIcon(tint = MaterialTheme.colorScheme.onSurface)
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
            // Layout
            SectionCard(title = stringResource(R.string.section_layout)) {
                LayoutRegistry.getAllNames().forEach { name ->
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

            // Size
            SectionCard(title = stringResource(R.string.section_size)) {
                Text(stringResource(R.string.height_label, heightScale), style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = heightScale,
                    onValueChange = { scope.launch { preferencesManager.setKeyboardHeight(it) } },
                    valueRange = 0.7f..1.5f, steps = 7
                )
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.width_label, widthPercent), style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = widthPercent.toFloat(),
                    onValueChange = { scope.launch { preferencesManager.setKeyboardWidth(it.toInt()) } },
                    valueRange = 50f..100f, steps = 9
                )
            }

            // Position
            SectionCard(title = stringResource(R.string.section_position)) {
                val options = listOf(
                    stringResource(R.string.position_left),
                    stringResource(R.string.position_center),
                    stringResource(R.string.position_right)
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

            // Bottom Padding
            SectionCard(title = stringResource(R.string.section_padding)) {
                Text(stringResource(R.string.padding_label, bottomPadding), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.padding_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = bottomPadding.toFloat(),
                    onValueChange = { scope.launch { preferencesManager.setBottomPadding(it.roundToInt()) } },
                    valueRange = 0f..60f, steps = 11
                )
            }

            // Theme
            SectionCard(title = stringResource(R.string.section_theme)) {
                SettingRow(stringResource(R.string.theme_follow_system), followSystemTheme) {
                    scope.launch { preferencesManager.setFollowSystemTheme(it) }
                }
                Text(stringResource(R.string.theme_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Predictions
            SectionCard(title = stringResource(R.string.section_predictions)) {
                SettingRow(stringResource(R.string.predictions_toggle), predictionsEnabled) {
                    scope.launch { preferencesManager.setPredictionsEnabled(it) }
                }
                if (predictionsEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.suggestions_label, suggestionCount), style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = suggestionCount.toFloat(),
                        onValueChange = { scope.launch { preferencesManager.setSuggestionCount(it.toInt()) } },
                        valueRange = 1f..5f, steps = 3
                    )
                }
            }

            // Clipboard
            SectionCard(title = stringResource(R.string.section_clipboard)) {
                SettingRow(stringResource(R.string.clipboard_toggle), clipboardEnabled) {
                    scope.launch { preferencesManager.setClipboardEnabled(it) }
                }
                Text(stringResource(R.string.clipboard_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Feedback
            SectionCard(title = stringResource(R.string.section_feedback)) {
                SettingRow(stringResource(R.string.haptic_toggle), haptic) {
                    scope.launch { preferencesManager.setHapticEnabled(it) }
                }
            }

            // About
            SectionCard(title = stringResource(R.string.section_about)) {
                Text(stringResource(R.string.about_version), style = MaterialTheme.typography.bodyMedium)
                Text(stringResource(R.string.about_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
