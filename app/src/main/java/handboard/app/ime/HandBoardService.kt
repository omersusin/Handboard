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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HandBoardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var prefs: PreferencesManager
    private val predictor = WordPredictor()
    private var clipboard: ClipboardHistory? = null

    private var isPasswordField = false
    private var isNumberField = false

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        prefs = PreferencesManager(this)
        
        val dictId = runBlocking { prefs.dictionaryId.first() }
        predictor.loadDictionary(this, dictId)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        val t = info?.inputType ?: 0
        val cls = t and InputType.TYPE_MASK_CLASS
        val v = t and InputType.TYPE_MASK_VARIATION
        isPasswordField = v == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
            v == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
            v == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
            (cls == InputType.TYPE_CLASS_NUMBER && (t and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0)
        isNumberField = cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE
    }

    override fun onFinishInputView(f: Boolean) {
        super.onFinishInputView(f)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    private fun getCurrentWord(): String {
        val ic = currentInputConnection ?: return ""
        val before = ic.getTextBeforeCursor(100, 0)?.toString() ?: return ""
        return predictor.getCurrentWord(before)
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

    private fun sendKey(code: Int, meta: Int = 0) {
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, code, 0, meta))
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, code, 0, meta))
    }

    private fun shouldAutoCapitalize(autoCapEnabled: Boolean): Boolean {
        if (!autoCapEnabled || isPasswordField) return false
        val ic = currentInputConnection ?: return false
        val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: return true
        if (before.isEmpty()) return true
        val trimmed = before.trimEnd()
        if (trimmed.isEmpty()) return before.isNotEmpty()
        return trimmed.last() in listOf('.', '!', '?', '\n')
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

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        return createComposeView(this, this) {
            val heightScale by prefs.keyboardHeight.collectAsState(initial = 1.0f)
            val widthPercent by prefs.keyboardWidth.collectAsState(initial = 100)
            val alignment by prefs.keyboardAlignment.collectAsState(initial = 1)
            val layoutName by prefs.selectedLayout.collectAsState(initial = "QWERTY")
            val hapticEnabled by prefs.hapticEnabled.collectAsState(initial = true)
            val soundEnabled by prefs.soundEnabled.collectAsState(initial = false)
            val suggestionCount by prefs.suggestionCount.collectAsState(initial = 3)
            val predictionsEnabled by prefs.predictionsEnabled.collectAsState(initial = true)
            val autocorrectEnabled by prefs.autocorrectEnabled.collectAsState(initial = true)
            val bottomPadding by prefs.bottomPadding.collectAsState(initial = 0)
            val clipboardEnabled by prefs.clipboardEnabled.collectAsState(initial = false)
            val followSystemTheme by prefs.followSystemTheme.collectAsState(initial = false)
            val numberRowEnabled by prefs.numberRowEnabled.collectAsState(initial = false)
            val autoCapitalize by prefs.autoCapitalize.collectAsState(initial = true)
            val spacebarCursor by prefs.spacebarCursor.collectAsState(initial = true)
            val largeKeys by prefs.largeKeys.collectAsState(initial = false)
            val dictId by prefs.dictionaryId.collectAsState(initial = "en_us")

            LaunchedEffect(clipboardEnabled) {
                if (clipboardEnabled && clipboard == null) {
                    clipboard = ClipboardHistory(this@HandBoardService)
                    clipboard?.initialize()
                } else if (!clipboardEnabled) {
                    clipboard?.destroy()
                    clipboard = null
                }
            }

            LaunchedEffect(dictId) {
                predictor.reloadDictionary(this@HandBoardService, dictId)
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
                suggestions.addAll(predictor.predict(word, suggestionCount))
            }

            fun commitText(text: String) {
                val ic = currentInputConnection ?: return
                val finalText = if (text.length == 1 && text[0].isLetter() && shouldAutoCapitalize(autoCapitalize)) {
                    text.uppercase()
                } else text
                ic.commitText(finalText, 1)

                if (text == " ") {
                    val word = getCurrentWord()
                    if (word.isNotEmpty()) predictor.onWordCommitted(word)
                }
                updateSuggestions()
            }

            fun applySuggestion(word: String) {
                val current = getCurrentWord()
                if (current.isNotEmpty()) {
                    currentInputConnection?.deleteSurroundingText(current.length, 0)
                }
                currentInputConnection?.commitText("$word ", 1)
                predictor.onWordCommitted(word)
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
                        preferencesManager = prefs,
                        heightScale = effectiveHeightScale,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        numberRowEnabled = numberRowEnabled,
                        spacebarCursor = spacebarCursor,
                        clipboardEnabled = clipboardEnabled,
                        clipboardHistory = if (clipboardEnabled) clipboard else null,
                        suggestionBar = if (showPredictions) {
                            {
                                SuggestionBar(
                                    suggestions = suggestions,
                                    onSuggestionClick = { applySuggestion(it) }
                                )
                            }
                        } else null,
                        onTextInput = { commitText(it) },
                        onBackspace = { performBackspace(); updateSuggestions() },
                        onEnter = {
                            val word = getCurrentWord()
                            if (word.isNotEmpty()) predictor.onWordCommitted(word)
                            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER)
                            suggestions.clear()
                        },
                        onEmojiInput = { currentInputConnection?.commitText(it, 1) },
                        onCursorMove = { dir -> moveCursor(dir) },
                        onCursorHome = { sendKey(KeyEvent.KEYCODE_MOVE_HOME) },
                        onCursorEnd = { sendKey(KeyEvent.KEYCODE_MOVE_END) },
                        onSelectAll = { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) },
                        onCopy = { currentInputConnection?.performContextMenuAction(android.R.id.copy) },
                        onCut = { currentInputConnection?.performContextMenuAction(android.R.id.cut) },
                        onPaste = { currentInputConnection?.performContextMenuAction(android.R.id.paste) },
                        onUndo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) },
                        onRedo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON) },
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
        clipboard?.destroy()
        clipboard = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }
}
