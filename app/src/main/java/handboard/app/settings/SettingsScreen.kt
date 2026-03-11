package handboard.app.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import handboard.app.R
import handboard.app.layout.LayoutRegistry
import handboard.app.layout.ui.BackArrowIcon
import handboard.app.prediction.DictionaryManager
import handboard.app.prediction.WordPredictor
import handboard.app.prediction.AospDictConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(preferencesManager: PreferencesManager, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val heightScale by preferencesManager.keyboardHeight.collectAsState(initial = 1.0f)
    val widthPercent by preferencesManager.keyboardWidth.collectAsState(initial = 100)
    val alignment by preferencesManager.keyboardAlignment.collectAsState(initial = 1)
    val layoutName by preferencesManager.selectedLayout.collectAsState(initial = "QWERTY")
    val haptic by preferencesManager.hapticEnabled.collectAsState(initial = true)
    val sound by preferencesManager.soundEnabled.collectAsState(initial = false)
    val suggestionCount by preferencesManager.suggestionCount.collectAsState(initial = 3)
    val predictionsEnabled by preferencesManager.predictionsEnabled.collectAsState(initial = true)
    val autocorrectEnabled by preferencesManager.autocorrectEnabled.collectAsState(initial = true)
    val bottomPadding by preferencesManager.bottomPadding.collectAsState(initial = 0)
    val clipboardEnabled by preferencesManager.clipboardEnabled.collectAsState(initial = false)
    val followSystemTheme by preferencesManager.followSystemTheme.collectAsState(initial = false)
    val numberRow by preferencesManager.numberRowEnabled.collectAsState(initial = false)
    val autoCap by preferencesManager.autoCapitalize.collectAsState(initial = true)
    val spacebarCursor by preferencesManager.spacebarCursor.collectAsState(initial = true)
    val highContrast by preferencesManager.highContrast.collectAsState(initial = false)
    val largeKeys by preferencesManager.largeKeys.collectAsState(initial = false)
    
    val searchEnabled by preferencesManager.searchEnabled.collectAsState(initial = true)
    val currencyEnabled by preferencesManager.currencyEnabled.collectAsState(initial = true)
    val kaomojiEnabled by preferencesManager.kaomojiEnabled.collectAsState(initial = true)
    val phrasesEnabled by preferencesManager.phrasesEnabled.collectAsState(initial = true)
    val translateEnabled by preferencesManager.translateEnabled.collectAsState(initial = true)
    val textEditingEnabled by preferencesManager.textEditingEnabled.collectAsState(initial = true)
    val emojiEnabled by preferencesManager.emojiEnabled.collectAsState(initial = true)

    val dictId by preferencesManager.dictionaryId.collectAsState(initial = "en_us")
    val multilingualEnabled by preferencesManager.multilingualEnabled.collectAsState(initial = false)
    val activeDicts by preferencesManager.activeDicts.collectAsState(initial = setOf("en_us"))

    var dictManager by remember { mutableStateOf(DictionaryManager(context)) }
    var loadedWordCount by remember { mutableStateOf(0) }
    var isImporting by remember { mutableStateOf(false) }
    val predictor = remember { WordPredictor() }
    val converter = remember { AospDictConverter(context) }
    
    LaunchedEffect(activeDicts, multilingualEnabled, dictId, dictManager) {
        withContext(Dispatchers.IO) {
            val toLoad = if (multilingualEnabled) activeDicts else setOf(dictId)
            predictor.loadDictionaries(context, toLoad)
            loadedWordCount = predictor.getDictionarySize()
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            isImporting = true
            scope.launch {
                converter.convertAndSave(uri)
                    .onSuccess { file ->
                        val count = file.useLines { it.count() }
                        Toast.makeText(context, "$count words imported successfully!", Toast.LENGTH_LONG).show()
                        dictManager = DictionaryManager(context) 
                    }
                    .onFailure { error -> Toast.makeText(context, "Error: ${error.localizedMessage}", Toast.LENGTH_LONG).show() }
                isImporting = false
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.settings_title)) }, navigationIcon = { IconButton(onClick = onBack) { BackArrowIcon(tint = MaterialTheme.colorScheme.onSurface) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            Sec("Keyboard Panels (Toolbar)") {
                Toggle("Web Search", searchEnabled) { scope.launch { preferencesManager.setSearchEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Translate", translateEnabled) { scope.launch { preferencesManager.setTranslateEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Currency Converter", currencyEnabled) { scope.launch { preferencesManager.setCurrencyEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Text Editing", textEditingEnabled) { scope.launch { preferencesManager.setTextEditingEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Clipboard History", clipboardEnabled) { scope.launch { preferencesManager.setClipboardEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Kaomoji (◕‿◕)", kaomojiEnabled) { scope.launch { preferencesManager.setKaomojiEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Emoji", emojiEnabled) { scope.launch { preferencesManager.setEmojiEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Quick Phrases", phrasesEnabled) { scope.launch { preferencesManager.setPhrasesEnabled(it) } }
                Sub("Turn off panels you don't use to save space on the toolbar.")
            }

            Sec("Dictionary & Language") {
                Toggle("Multilingual Typing", multilingualEnabled) { scope.launch { preferencesManager.setMultilingualEnabled(it) } }
                Sub("Combine multiple dictionaries for simultaneous prediction")
                Spacer(Modifier.height(12.dp))

                dictManager.getAvailable().forEach { dict ->
                    Row(Modifier.fillMaxWidth().clickable { scope.launch { if (multilingualEnabled) { val newSet = activeDicts.toMutableSet(); if (newSet.contains(dict.id)) newSet.remove(dict.id) else newSet.add(dict.id); if (newSet.isEmpty()) newSet.add(dict.id); preferencesManager.setActiveDicts(newSet) } else { preferencesManager.setDictionaryId(dict.id) } } }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (multilingualEnabled) Checkbox(checked = activeDicts.contains(dict.id), onCheckedChange = null)
                        else RadioButton(selected = dict.id == dictId, onClick = null)
                        Spacer(Modifier.width(8.dp))
                        Text(dict.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Sub("Engine currently loaded with $loadedWordCount valid words.")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { filePicker.launch("*/*") }, modifier = Modifier.weight(1f), enabled = !isImporting) { Text(if (isImporting) "Converting..." else "Import File") }
                    Button(onClick = { dictManager.deleteCustomDictionaries(); dictManager = DictionaryManager(context); Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f)) { Text("Delete Imports") }
                }
            }

            Sec(stringResource(R.string.section_predictions)) { Toggle(stringResource(R.string.predictions_toggle), predictionsEnabled) { scope.launch { preferencesManager.setPredictionsEnabled(it) } }; if (predictionsEnabled) { Spacer(Modifier.height(8.dp)); Toggle("Auto-Correct", autocorrectEnabled) { scope.launch { preferencesManager.setAutocorrectEnabled(it) } }; Spacer(Modifier.height(16.dp)); Text(stringResource(R.string.suggestions_label, suggestionCount)); Slider(value = suggestionCount.toFloat(), onValueChange = { scope.launch { preferencesManager.setSuggestionCount(it.toInt()) } }, valueRange = 1f..5f, steps = 3) } }
            Sec("Personal Dictionary") { Text("${predictor.getPersonalWords().size} learned words", style = MaterialTheme.typography.bodyLarge); Spacer(Modifier.height(8.dp)); Button(onClick = { predictor.clearPersonalDictionary() }) { Text("Clear Personal Dictionary") } }
            Sec(stringResource(R.string.section_layout)) { LayoutRegistry.getAllNames().forEach { name -> Row(Modifier.fillMaxWidth().clickable { scope.launch { preferencesManager.setSelectedLayout(name) } }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = name == layoutName, onClick = null); Spacer(Modifier.width(8.dp)); Text(name, style = MaterialTheme.typography.bodyLarge) } } }
            Sec(stringResource(R.string.section_size)) { Text(stringResource(R.string.height_label, heightScale)); Slider(value = heightScale, onValueChange = { scope.launch { preferencesManager.setKeyboardHeight(it) } }, valueRange = 0.7f..1.5f, steps = 7); Spacer(Modifier.height(8.dp)); Text(stringResource(R.string.width_label, widthPercent)); Slider(value = widthPercent.toFloat(), onValueChange = { scope.launch { preferencesManager.setKeyboardWidth(it.toInt()) } }, valueRange = 50f..100f, steps = 9) }
            Sec(stringResource(R.string.section_position)) { val opts = listOf(stringResource(R.string.position_left), stringResource(R.string.position_center), stringResource(R.string.position_right)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { opts.forEachIndexed { i, l -> FilterChip(selected = i == alignment, onClick = { scope.launch { preferencesManager.setKeyboardAlignment(i) } }, label = { Text(l) }, modifier = Modifier.weight(1f)) } } }
            Sec(stringResource(R.string.section_padding)) { Text(stringResource(R.string.padding_label, bottomPadding)); Sub(stringResource(R.string.padding_desc)); Slider(value = bottomPadding.toFloat(), onValueChange = { scope.launch { preferencesManager.setBottomPadding(it.roundToInt()) } }, valueRange = 0f..60f, steps = 11) }
            Sec(stringResource(R.string.section_typing)) { Toggle(stringResource(R.string.number_row_toggle), numberRow) { scope.launch { preferencesManager.setNumberRowEnabled(it) } }; Sub(stringResource(R.string.number_row_desc)); Spacer(Modifier.height(8.dp)); Toggle(stringResource(R.string.auto_capitalize_toggle), autoCap) { scope.launch { preferencesManager.setAutoCapitalize(it) } }; Sub(stringResource(R.string.auto_capitalize_desc)); Spacer(Modifier.height(8.dp)); Toggle(stringResource(R.string.spacebar_cursor_toggle), spacebarCursor) { scope.launch { preferencesManager.setSpacebarCursor(it) } }; Sub(stringResource(R.string.spacebar_cursor_desc)) }
            Sec(stringResource(R.string.section_theme)) { Toggle(stringResource(R.string.theme_follow_system), followSystemTheme) { scope.launch { preferencesManager.setFollowSystemTheme(it) } }; Sub(stringResource(R.string.theme_desc)) }
            Sec(stringResource(R.string.section_feedback)) { Toggle(stringResource(R.string.haptic_toggle), haptic) { scope.launch { preferencesManager.setHapticEnabled(it) } }; Spacer(Modifier.height(8.dp)); Toggle(stringResource(R.string.sound_toggle), sound) { scope.launch { preferencesManager.setSoundEnabled(it) } }; Sub(stringResource(R.string.sound_desc)) }
            Sec(stringResource(R.string.section_accessibility)) { Toggle(stringResource(R.string.high_contrast_toggle), highContrast) { scope.launch { preferencesManager.setHighContrast(it) } }; Sub(stringResource(R.string.high_contrast_desc)); Spacer(Modifier.height(8.dp)); Toggle(stringResource(R.string.large_keys_toggle), largeKeys) { scope.launch { preferencesManager.setLargeKeys(it) } }; Sub(stringResource(R.string.large_keys_desc)) }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable private fun Toggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(label, style = MaterialTheme.typography.bodyLarge); Switch(checked = checked, onCheckedChange = onChange) } }
@Composable private fun Sub(text: String) { Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
@Composable private fun Sec(title: String, content: @Composable () -> Unit) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(16.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(12.dp)); content() } } }
