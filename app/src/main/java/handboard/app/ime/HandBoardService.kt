package handboard.app.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import handboard.app.core.theme.HandBoardTheme
import handboard.app.layout.LayoutSwitcher
import handboard.app.layout.ui.KeyboardView
import handboard.app.layout.ui.KeyboardWrapper
import handboard.app.prediction.SuggestionBar
import handboard.app.prediction.WordPredictor
import handboard.app.settings.PreferencesManager

class HandBoardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var preferencesManager: PreferencesManager
    private val wordPredictor = WordPredictor()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        preferencesManager = PreferencesManager(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        return createComposeView(this, this) {
            val heightScale by preferencesManager.keyboardHeight.collectAsState(initial = 1.0f)
            val widthPercent by preferencesManager.keyboardWidth.collectAsState(initial = 100)
            val alignment by preferencesManager.keyboardAlignment.collectAsState(initial = 1)
            val layoutName by preferencesManager.selectedLayout.collectAsState(initial = "QWERTY")
            val hapticEnabled by preferencesManager.hapticEnabled.collectAsState(initial = true)
            val suggestionCount by preferencesManager.suggestionCount.collectAsState(initial = 3)
            val predictionsEnabled by preferencesManager.predictionsEnabled.collectAsState(initial = true)
            val bottomPadding by preferencesManager.bottomPadding.collectAsState(initial = 0)

            val layoutSwitcher = remember { LayoutSwitcher(layoutName) }
            LaunchedEffect(layoutName) { layoutSwitcher.setLayout(layoutName) }

            var currentText by remember { mutableStateOf("") }
            var suggestions by remember { mutableStateOf(emptyList<String>()) }

            fun updateSuggestions() {
                if (!predictionsEnabled) {
                    suggestions = emptyList()
                    return
                }
                val word = wordPredictor.getCurrentWordFromText(currentText)
                suggestions = wordPredictor.predict(word, suggestionCount)
            }

            fun commitText(text: String) {
                currentInputConnection?.commitText(text, 1)
                currentText += text
                if (text == " ") {
                    val words = currentText.trim().split(" ")
                    val lastWord = words.lastOrNull() ?: ""
                    if (lastWord.isNotEmpty()) wordPredictor.learnWord(lastWord)
                }
                updateSuggestions()
            }

            fun applySuggestion(word: String) {
                val current = wordPredictor.getCurrentWordFromText(currentText)
                if (current.isNotEmpty()) {
                    currentInputConnection?.deleteSurroundingText(current.length, 0)
                }
                currentInputConnection?.commitText("$word ", 1)
                currentText = currentText.dropLast(current.length) + "$word "
                wordPredictor.learnWord(word)
                updateSuggestions()
            }

            HandBoardTheme(darkTheme = true) {
                KeyboardWrapper(
                    widthFraction = widthPercent / 100f,
                    alignment = alignment
                ) {
                    KeyboardView(
                        layoutSwitcher = layoutSwitcher,
                        heightScale = heightScale,
                        hapticEnabled = hapticEnabled,
                        suggestionBar = if (predictionsEnabled) {
                            {
                                SuggestionBar(
                                    suggestions = suggestions,
                                    onSuggestionClick = { applySuggestion(it) }
                                )
                            }
                        } else null,
                        onTextInput = { text -> commitText(text) },
                        onBackspace = {
                            currentInputConnection?.deleteSurroundingText(1, 0)
                            if (currentText.isNotEmpty()) {
                                currentText = currentText.dropLast(1)
                            }
                            updateSuggestions()
                        },
                        onEnter = {
                            sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_ENTER)
                            currentText = ""
                            suggestions = emptyList()
                        }
                    )

                    // Extra bottom padding (user adjustable)
                    if (bottomPadding > 0) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(bottomPadding.dp))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }
}
