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
    private lateinit var prefs: PreferencesManager
    private val predictor = WordPredictor()
    private var clipboard: ClipboardHistory? = null
    private var isPassword = false
    private var isNumber = false

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        prefs = PreferencesManager(this)
        predictor.loadDictionary(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        val t = info?.inputType ?: 0
        val cls = t and InputType.TYPE_MASK_CLASS
        val v = t and InputType.TYPE_MASK_VARIATION
        isPassword = v == InputType.TYPE_TEXT_VARIATION_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
        isNumber = cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE
    }

    override fun onFinishInputView(f: Boolean) { super.onFinishInputView(f); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }

    private fun getWord(): String {
        val b = currentInputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: return ""
        return predictor.getCurrentWord(b)
    }

    private fun doBackspace() {
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ic.deleteSurroundingTextInCodePoints(1, 0)
        else {
            val b = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""
            if (b.length >= 2 && Character.isLowSurrogate(b.last()) && Character.isHighSurrogate(b[b.length - 2])) ic.deleteSurroundingText(2, 0)
            else ic.deleteSurroundingText(1, 0)
        }
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

    private fun shouldCap(auto: Boolean): Boolean {
        if (!auto || isPassword) return false
        val b = currentInputConnection?.getTextBeforeCursor(2, 0)?.toString() ?: return true
        if (b.isEmpty()) return true
        val t = b.trimEnd()
        return t.isEmpty() || t.last() in listOf('.', '!', '?', '\n')
    }

    private fun pasteImg(item: ClipboardItem) {
        val uri = item.imageUri ?: return
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val d = android.content.ClipDescription("image", arrayOf(item.mimeType))
                ic.commitContent(InputContentInfo(uri, d, null), InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null)
            } catch (_: Exception) { item.text?.let { ic.commitText(it, 1) } }
        } else item.text?.let { ic.commitText(it, 1) }
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        return createComposeView(this, this) {
            val hs by prefs.keyboardHeight.collectAsState(initial = 1.0f)
            val wp by prefs.keyboardWidth.collectAsState(initial = 100)
            val al by prefs.keyboardAlignment.collectAsState(initial = 1)
            val ln by prefs.selectedLayout.collectAsState(initial = "QWERTY")
            val hap by prefs.hapticEnabled.collectAsState(initial = true)
            val snd by prefs.soundEnabled.collectAsState(initial = false)
            val sc by prefs.suggestionCount.collectAsState(initial = 3)
            val pe by prefs.predictionsEnabled.collectAsState(initial = true)
            val bp by prefs.bottomPadding.collectAsState(initial = 0)
            val ce by prefs.clipboardEnabled.collectAsState(initial = false)
            val fst by prefs.followSystemTheme.collectAsState(initial = false)
            val nr by prefs.numberRowEnabled.collectAsState(initial = false)
            val ac by prefs.autoCapitalize.collectAsState(initial = true)
            val sc2 by prefs.spacebarCursor.collectAsState(initial = true)
            val lk by prefs.largeKeys.collectAsState(initial = false)

            LaunchedEffect(ce) {
                if (ce && clipboard == null) { clipboard = ClipboardHistory(this@HandBoardService); clipboard?.initialize() }
                else if (!ce) { clipboard?.destroy(); clipboard = null }
            }

            val ls = remember { LayoutSwitcher(ln) }
            LaunchedEffect(ln) { ls.setLayout(ln) }

            val sugs = remember { mutableStateListOf<String>() }
            val showPred = pe && !isPassword && !isNumber
            val effH = if (lk) hs * 1.25f else hs

            fun updateSugs() { sugs.clear(); if (showPred) sugs.addAll(predictor.predict(getWord(), sc)) }

            fun commit(text: String) {
                val ic = currentInputConnection ?: return
                val final = if (text.length == 1 && text[0].isLetter() && shouldCap(ac)) text.uppercase() else text
                ic.commitText(final, 1)
                if (text == " ") { val w = getWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w) }
                updateSugs()
            }

            fun applySug(word: String) {
                val cur = getWord()
                if (cur.isNotEmpty()) currentInputConnection?.deleteSurroundingText(cur.length, 0)
                currentInputConnection?.commitText("$word ", 1)
                predictor.onWordCommitted(word)
                updateSugs()
            }

            val useDark = if (fst) isSystemInDarkTheme() else true

            HandBoardTheme(darkTheme = useDark) {
                KeyboardWrapper(widthFraction = wp / 100f, alignment = al) {
                    KeyboardView(
                        layoutSwitcher = ls, preferencesManager = prefs,
                        heightScale = effH, hapticEnabled = hap, soundEnabled = snd,
                        numberRowEnabled = nr, spacebarCursor = sc2,
                        clipboardEnabled = ce, clipboardHistory = if (ce) clipboard else null,
                        suggestionBar = if (showPred) { { SuggestionBar(suggestions = sugs, onSuggestionClick = { applySug(it) }) } } else null,
                        onTextInput = { commit(it) },
                        onBackspace = { doBackspace(); updateSugs() },
                        onEnter = {
                            val w = getWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w)
                            sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER); sugs.clear()
                        },
                        onEmojiInput = { currentInputConnection?.commitText(it, 1) },
                        onCursorMove = { moveCursor(it) },
                        onCursorHome = { sendKey(KeyEvent.KEYCODE_MOVE_HOME) },
                        onCursorEnd = { sendKey(KeyEvent.KEYCODE_MOVE_END) },
                        onSelectAll = { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) },
                        onCopy = { currentInputConnection?.performContextMenuAction(android.R.id.copy) },
                        onCut = { currentInputConnection?.performContextMenuAction(android.R.id.cut) },
                        onPaste = { currentInputConnection?.performContextMenuAction(android.R.id.paste) },
                        onUndo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) },
                        onRedo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON) },
                        onPasteImage = { pasteImg(it) }
                    )
                    if (bp > 0) Spacer(Modifier.fillMaxWidth().height(bp.dp))
                }
            }
        }
    }

    override fun onDestroy() { clipboard?.destroy(); clipboard = null; lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY); super.onDestroy() }
}
