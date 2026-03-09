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
import androidx.compose.material3.MaterialTheme
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
import handboard.app.core.theme.applyKeyboardTheme
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
        val v = t and InputType.TYPE_MASK_VARIATION
        val cls = t and InputType.TYPE_MASK_CLASS
        isPasswordField = v == InputType.TYPE_TEXT_VARIATION_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD || (cls == InputType.TYPE_CLASS_NUMBER && (t and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0)
        isNumberField = cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE
    }

    override fun onFinishInputView(f: Boolean) {
        super.onFinishInputView(f)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    private fun getCurrentWord(): String {
        val b = currentInputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: return ""
        return predictor.getCurrentWord(b)
    }

    private fun performBackspace() {
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ic.deleteSurroundingTextInCodePoints(1, 0)
        else ic.deleteSurroundingText(1, 0)
    }

    private fun moveCursor(d: Int) {
        val c = if (d > 0) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, c))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, c))
    }

    private fun sendKey(code: Int, meta: Int = 0) {
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, code, 0, meta))
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, code, 0, meta))
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        return createComposeView(this, this) {
            val themePref by prefs.themePreference.collectAsState(initial = "system")
            val isSysDark = isSystemInDarkTheme()
            val useDark = when (themePref) {
                "light" -> false
                "dark", "amoled" -> true
                else -> isSysDark
            }

            HandBoardTheme(darkTheme = useDark) {
                applyKeyboardTheme(themePref, isSysDark, MaterialTheme.colorScheme.primary)
                
                val hs by prefs.keyboardHeight.collectAsState(initial = 1.0f)
                val wp by prefs.keyboardWidth.collectAsState(initial = 100)
                val al by prefs.keyboardAlignment.collectAsState(initial = 1)
                val ln by prefs.selectedLayout.collectAsState(initial = "QWERTY")
                val hap by prefs.hapticEnabled.collectAsState(initial = true)
                val snd by prefs.soundEnabled.collectAsState(initial = false)
                val sc by prefs.suggestionCount.collectAsState(initial = 3)
                val pe by prefs.predictionsEnabled.collectAsState(initial = true)
                val acEnabled by prefs.autocorrectEnabled.collectAsState(initial = true)
                val bp by prefs.bottomPadding.collectAsState(initial = 0)
                val ce by prefs.clipboardEnabled.collectAsState(initial = false)
                val nr by prefs.numberRowEnabled.collectAsState(initial = false)
                val ac by prefs.autoCapitalize.collectAsState(initial = true)
                val sc2 by prefs.spacebarCursor.collectAsState(initial = true)
                val lk by prefs.largeKeys.collectAsState(initial = false)
                val dictId by prefs.dictionaryId.collectAsState(initial = "en_us")

                LaunchedEffect(ce) {
                    if (ce && clipboard == null) { clipboard = ClipboardHistory(this@HandBoardService); clipboard?.initialize() }
                    else if (!ce) { clipboard?.destroy(); clipboard = null }
                }
                LaunchedEffect(dictId) { predictor.reloadDictionary(this@HandBoardService, dictId) }
                val ls = remember { LayoutSwitcher(ln) }
                LaunchedEffect(ln) { ls.setLayout(ln) }

                val sugs = remember { mutableStateListOf<String>() }
                val showPred = pe && !isPasswordField && !isNumberField

                fun updateSuggestions() {
                    sugs.clear(); if (!showPred) return
                    sugs.addAll(predictor.predict(getCurrentWord(), sc, acEnabled))
                }

                KeyboardWrapper(widthFraction = wp / 100f, alignment = al) {
                    KeyboardView(
                        layoutSwitcher = ls, preferencesManager = prefs, heightScale = if (lk) hs * 1.25f else hs,
                        hapticEnabled = hap, soundEnabled = snd, numberRowEnabled = nr, spacebarCursor = sc2,
                        clipboardEnabled = ce, clipboardHistory = if (ce) clipboard else null,
                        suggestionBar = if (showPred) { { SuggestionBar(suggestions = sugs, onSuggestionClick = { 
                            val cur = getCurrentWord()
                            if (cur.isNotEmpty()) currentInputConnection?.deleteSurroundingText(cur.length, 0)
                            currentInputConnection?.commitText("$it ", 1)
                            predictor.onWordCommitted(it)
                            updateSuggestions()
                        }) } } else null,
                        onTextInput = { text ->
                            val final = if (text.length == 1 && text[0].isLetter() && ac && !isPasswordField) {
                                val b = currentInputConnection?.getTextBeforeCursor(2, 0)?.toString() ?: ""
                                if (b.isEmpty() || b.trimEnd().lastOrNull() in listOf('.', '!', '?', '\n')) text.uppercase() else text
                            } else text
                            currentInputConnection?.commitText(final, 1)
                            if (text == " ") { val w = getCurrentWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w) }
                            updateSuggestions()
                        },
                        onBackspace = { performBackspace(); updateSuggestions() },
                        onEnter = { val w = getCurrentWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w); sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER); sugs.clear() },
                        onEmojiInput = { currentInputConnection?.commitText(it, 1) },
                        onCursorMove = { moveCursor(it) }, onCursorHome = { sendKey(KeyEvent.KEYCODE_MOVE_HOME) }, onCursorEnd = { sendKey(KeyEvent.KEYCODE_MOVE_END) },
                        onSelectAll = { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) }, onCopy = { currentInputConnection?.performContextMenuAction(android.R.id.copy) }, onCut = { currentInputConnection?.performContextMenuAction(android.R.id.cut) }, onPaste = { currentInputConnection?.performContextMenuAction(android.R.id.paste) },
                        onUndo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) }, onRedo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON) }
                    )
                    if (bp > 0) Spacer(Modifier.fillMaxWidth().height(bp.dp))
                }
            }
        }
    }
    override fun onDestroy() { clipboard?.destroy(); clipboard = null; super.onDestroy() }
}
