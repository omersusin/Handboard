package handboard.app.ime

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputContentInfo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import handboard.app.clipboard.ClipboardHistory
import handboard.app.clipboard.ClipboardItem
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
    private var clipboardHistory: ClipboardHistory? = null

    private var isPasswordField = false
    private var isNumberField = false

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        preferencesManager = PreferencesManager(this)
        wordPredictor.loadDictionary(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val inputType = info?.inputType ?: 0
        val cls = inputType and InputType.TYPE_MASK_CLASS
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        isPasswordField = variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
            (cls == InputType.TYPE_CLASS_NUMBER && (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0)
        isNumberField = cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    private fun getCurrentWord(): String {
        val ic = currentInputConnection ?: return ""
        val before = ic.getTextBeforeCursor(100, 0)?.toString() ?: return ""
        return wordPredictor.getCurrentWord(before)
    }

    private fun performBackspace() {
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ic.deleteSurroundingTextInCodePoints(1, 0)
        } else {
            val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""
            if (before.length >= 2 && Character.isLowSurrogate(before.last()) &&
                Character.isHighSurrogate(before[before.length - 2])) {
                ic.deleteSurroundingText(2, 0)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        }
    }

    private fun moveCursor(direction: Int) {
        val ic = currentInputConnection ?: return
        val code = if (direction > 0) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
    }

    private fun moveCursorHome() {
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_HOME))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MOVE_HOME))
    }

    private fun moveCursorEnd() {
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_END))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MOVE_END))
    }

    private fun selectAll() {
        currentInputConnection?.performContextMenuAction(android.R.id.selectAll)
    }

    private fun copyText() {
        currentInputConnection?.performContextMenuAction(android.R.id.copy)
    }

    private fun cutText() {
        currentInputConnection?.performContextMenuAction(android.R.id.cut)
    }

    private fun pasteText() {
        currentInputConnection?.performContextMenuAction(android.R.id.paste)
    }

    private fun undoAction() {
        currentInputConnection?.sendKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON))
        currentInputConnection?.sendKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON))
    }

    private fun redoAction() {
        currentInputConnection?.sendKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0,
                KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
        currentInputConnection?.sendKeyEvent(
            KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_Z, 0,
                KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON))
    }

    private fun pasteImage(item: ClipboardItem) {
        val uri = item.imageUri ?: return
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val desc = android.content.ClipDescription("image", arrayOf(item.mimeType))
                val info = InputContentInfo(uri, desc, null)
                ic.commitContent(info, InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null)
            } catch (_: Exception) { item.text?.let { ic.commitText(it, 1) } }
        } else {
            item.text?.let { ic.commitText(it, 1) }
        }
    }

    private fun shouldAutoCapitalize(autoCapEnabled: Boolean): Boolean {
        if (!autoCapEnabled || isPasswordField) return false
        val ic = currentInputConnection ?: return false
        val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: return true
        if (before.isEmpty()) return true
        val trimmed = before.trimEnd()
        if (trimmed.isEmpty()) return before.isNotEmpty() // start of field after spaces
        return trimmed.last() in listOf('.', '!', '?', '\n')
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        return createComposeView(this, this) {
            val heightScale by preferencesManager.keyboardHeight.collectAsState(initial = 1.0f)
            val widthPercent by preferencesManager.keyboardWidth.collectAsState(initial = 100)
            val alignment by preferencesManager.keyboardAlignment.collectAsState(initial = 1)
            val layoutName by preferencesManager.selectedLayout.collectAsState(initial = "QWERTY")
            val hapticEnabled by preferencesManager.hapticEnabled.collectAsState(initial = true)
            val soundEnabled by preferencesManager.soundEnabled.collectAsState(initial = false)
            val suggestionCount by preferencesManager.suggestionCount.collectAsState(initial = 3)
            val predictionsEnabled by preferencesManager.predictionsEnabled.collectAsState(initial = true)
            val bottomPadding by preferencesManager.bottomPadding.collectAsState(initial = 0)
            val clipboardEnabled by preferencesManager.clipboardEnabled.collectAsState(initial = false)
            val followSystemTheme by preferencesManager.followSystemTheme.collectAsState(initial = false)
            val numberRowEnabled by preferencesManager.numberRowEnabled.collectAsState(initial = false)
            val autoCapitalize by preferencesManager.autoCapitalize.collectAsState(initial = true)
            val spacebarCursor by preferencesManager.spacebarCursor.collectAsState(initial = true)
            val largeKeys by preferencesManager.largeKeys.collectAsState(initial = false)

            LaunchedEffect(clipboardEnabled) {
                if (clipboardEnabled && clipboardHistory == null) {
                    clipboardHistory = ClipboardHistory(this@HandBoardService)
                    clipboardHistory?.initialize()
                } else if (!clipboardEnabled) {
                    clipboardHistory?.destroy()
                    clipboardHistory = null
                }
            }

            val layoutSwitcher = remember { LayoutSwitcher(layoutName) }
            LaunchedEffect(layoutName) { layoutSwitcher.setLayout(layoutName) }

            val suggestions = remember { mutableStateListOf<String>() }
            val showPredictions = predictionsEnabled && !isPasswordField && !isNumberField

            val effectiveHeightScale = if (largeKeys) heightScale * 1.25f else heightScale

            fun updateSuggestions() {
                suggestions.clear()
                if (!showPredictions) return
                val word = getCurrentWord()
                suggestions.addAll(wordPredictor.predict(word, suggestionCount))
            }

            fun commitText(text: String) {
                val ic = currentInputConnection ?: return

                // Auto-capitalize
                val finalText = if (text.length == 1 && text[0].isLetter() &&
                    shouldAutoCapitalize(autoCapitalize)) {
                    text.uppercase()
                } else text

                ic.commitText(finalText, 1)

                if (text == " ") {
                    val word = getCurrentWord()
                    if (word.isNotEmpty()) wordPredictor.onWordCommitted(word)
                }
                updateSuggestions()
            }

            fun applySuggestion(word: String) {
                val current = getCurrentWord()
                if (current.isNotEmpty()) {
                    currentInputConnection?.deleteSurroundingText(current.length, 0)
                }
                currentInputConnection?.commitText("$word ", 1)
                wordPredictor.onWordCommitted(word)
                updateSuggestions()
            }

            val useDark = if (followSystemTheme) isSystemInDarkTheme() else true

            HandBoardTheme(darkTheme = useDark) {
                KeyboardWrapper(
                    widthFraction = widthPercent / 100f,
                    alignment = alignment
                ) {
                    KeyboardView(
                        layoutSwitcher = layoutSwitcher,
                        preferencesManager = preferencesManager,
                        heightScale = effectiveHeightScale,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        numberRowEnabled = numberRowEnabled,
                        spacebarCursor = spacebarCursor,
                        clipboardEnabled = clipboardEnabled,
                        clipboardHistory = if (clipboardEnabled) clipboardHistory else null,
                        suggestionBar = if (showPredictions) {
                            {
                                SuggestionBar(
                                    suggestions = suggestions,
                                    onSuggestionClick = { applySuggestion(it) }
                                )
                            }
                        } else null,
                        onTextInput = { commitText(it) },
                        onBackspace = {
                            performBackspace()
                            updateSuggestions()
                        },
                        onEnter = {
                            val word = getCurrentWord()
                            if (word.isNotEmpty()) wordPredictor.onWordCommitted(word)
                            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                            suggestions.clear()
                        },
                        onEmojiInput = { emoji ->
                            currentInputConnection?.commitText(emoji, 1)
                        },
                        onCursorMove = { dir -> moveCursor(dir) },
                        onCursorHome = { moveCursorHome() },
                        onCursorEnd = { moveCursorEnd() },
                        onSelectAll = { selectAll() },
                        onCopy = { copyText() },
                        onCut = { cutText() },
                        onPaste = { pasteText() },
                        onUndo = { undoAction() },
                        onRedo = { redoAction() },
                        onPasteImage = { pasteImage(it) }
                    )

                    if (bottomPadding > 0) {
                        Spacer(modifier = Modifier.fillMaxWidth().height(bottomPadding.dp))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        clipboardHistory?.destroy()
        clipboardHistory = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }
}
