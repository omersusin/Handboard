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
    val followSystemTheme by preferencesManager.followSystemTheme.collectAsState(initial = false)
    val numberRow by preferencesManager.numberRowEnabled.collectAsState(initial = false)
    val autoCap by preferencesManager.autoCapitalize.collectAsState(initial = true)
    val spacebarCursor by preferencesManager.spacebarCursor.collectAsState(initial = true)
    val highContrast by preferencesManager.highContrast.collectAsState(initial = false)
    val largeKeys by preferencesManager.largeKeys.collectAsState(initial = false)
    
    // Panels
    val clipboardEnabled by preferencesManager.clipboardEnabled.collectAsState(initial = true)
    val searchEnabled by preferencesManager.searchEnabled.collectAsState(initial = true)
    val currencyEnabled by preferencesManager.currencyEnabled.collectAsState(initial = true)
    val kaomojiEnabled by preferencesManager.kaomojiEnabled.collectAsState(initial = true)
    val phrasesEnabled by preferencesManager.phrasesEnabled.collectAsState(initial = true)

    val dictId by preferencesManager.dictionaryId.collectAsState(initial = "en_us")
    val multilingualEnabled by preferencesManager.multilingualEnabled.collectAsState(initial = false)
    val activeDicts by preferencesManager.activeDicts.collectAsState(initial = setOf("en_us"))

    var dictManager by remember { mutableStateOf(DictionaryManager(context)) }
    var loadedWordCount by remember { mutableStateOf(0) }
    val predictor = remember { WordPredictor() }
    
    LaunchedEffect(activeDicts, multilingualEnabled, dictId, dictManager) {
        withContext(Dispatchers.IO) {
            val toLoad = if (multilingualEnabled) activeDicts else setOf(dictId)
            predictor.loadDictionaries(context, toLoad)
            loadedWordCount = predictor.getDictionarySize()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onClick = onBack) { BackArrowIcon(tint = MaterialTheme.colorScheme.onSurface) } }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            // Paneller (Kapat/Aç)
            Sec("Keyboard Panels (Toolbar)") {
                Toggle("Web Search", searchEnabled) { scope.launch { preferencesManager.setSearchEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Currency Converter", currencyEnabled) { scope.launch { preferencesManager.setCurrencyEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Clipboard History", clipboardEnabled) { scope.launch { preferencesManager.setClipboardEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Kaomoji (◕‿◕)", kaomojiEnabled) { scope.launch { preferencesManager.setKaomojiEnabled(it) } }
                Spacer(Modifier.height(8.dp))
                Toggle("Quick Phrases", phrasesEnabled) { scope.launch { preferencesManager.setPhrasesEnabled(it) } }
                Sub("Turn off panels you don't use to save space on the toolbar.")
            }

            // Predictions
            Sec("Predictions") { Toggle("Word Predictions", predictionsEnabled) { scope.launch { preferencesManager.setPredictionsEnabled(it) } }; if (predictionsEnabled) { Spacer(Modifier.height(8.dp)); Toggle("Auto-Correct", autocorrectEnabled) { scope.launch { preferencesManager.setAutocorrectEnabled(it) } }; Spacer(Modifier.height(16.dp)); Text("Suggestions: $suggestionCount"); Slider(value = suggestionCount.toFloat(), onValueChange = { scope.launch { preferencesManager.setSuggestionCount(it.toInt()) } }, valueRange = 1f..5f, steps = 3) } }
            Sec("Personal Dictionary") { Text("${predictor.getPersonalWords().size} learned words", style = MaterialTheme.typography.bodyLarge); Spacer(Modifier.height(8.dp)); Button(onClick = { predictor.clearPersonalDictionary() }) { Text("Clear Personal Dictionary") } }
            Sec("Layout") { LayoutRegistry.getAllNames().forEach { name -> Row(Modifier.fillMaxWidth().clickable { scope.launch { preferencesManager.setSelectedLayout(name) } }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = name == layoutName, onClick = null); Spacer(Modifier.width(8.dp)); Text(name, style = MaterialTheme.typography.bodyLarge) } } }
            Sec("Size & Position") { Text("Height: $heightScale x"); Slider(value = heightScale, onValueChange = { scope.launch { preferencesManager.setKeyboardHeight(it) } }, valueRange = 0.7f..1.5f, steps = 7); Spacer(Modifier.height(8.dp)); Text("Width: $widthPercent%"); Slider(value = widthPercent.toFloat(), onValueChange = { scope.launch { preferencesManager.setKeyboardWidth(it.toInt()) } }, valueRange = 50f..100f, steps = 9) }
            Sec("Typing") { Toggle("Number Row", numberRow) { scope.launch { preferencesManager.setNumberRowEnabled(it) } }; Spacer(Modifier.height(8.dp)); Toggle("Auto-Capitalize", autoCap) { scope.launch { preferencesManager.setAutoCapitalize(it) } }; Spacer(Modifier.height(8.dp)); Toggle("Spacebar Cursor", spacebarCursor) { scope.launch { preferencesManager.setSpacebarCursor(it) } } }
            Sec("Feedback") { Toggle("Haptic Feedback", haptic) { scope.launch { preferencesManager.setHapticEnabled(it) } }; Spacer(Modifier.height(8.dp)); Toggle("Key Sound", sound) { scope.launch { preferencesManager.setSoundEnabled(it) } } }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable private fun Toggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(label, style = MaterialTheme.typography.bodyLarge); Switch(checked = checked, onCheckedChange = onChange) } }
@Composable private fun Sub(text: String) { Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
@Composable private fun Sec(title: String, content: @Composable () -> Unit) { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Column(Modifier.padding(16.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(12.dp)); content() } } }
