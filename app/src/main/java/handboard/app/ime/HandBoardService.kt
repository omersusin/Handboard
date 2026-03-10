package handboard.app.ime

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputContentInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.lifecycle.*
import androidx.savedstate.*
import handboard.app.MainActivity
import handboard.app.clipboard.ClipboardHistory
import handboard.app.clipboard.ClipboardItem
import handboard.app.core.theme.*
import handboard.app.layout.LayoutSwitcher
import handboard.app.layout.ui.KeyboardView
import handboard.app.layout.ui.KeyboardWrapper
import handboard.app.prediction.*
import handboard.app.settings.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HandBoardService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()
    
    private lateinit var prefs: PreferencesManager
    private val predictor = WordPredictor()
    private var clipboard: ClipboardHistory? = null
    private var isPasswordField = false
    private var isNumberField = false
    private var lastSpaceTime = 0L

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        prefs = PreferencesManager(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        val t = info?.inputType ?: 0
        val v = t and InputType.TYPE_MASK_VARIATION
        val cls = t and InputType.TYPE_MASK_CLASS
        isPasswordField = v == InputType.TYPE_TEXT_VARIATION_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || v == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD || (cls == InputType.TYPE_CLASS_NUMBER && (t and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0)
        isNumberField = cls == InputType.TYPE_CLASS_NUMBER || cls == InputType.TYPE_CLASS_PHONE
        lastSpaceTime = 0L
    }

    override fun onFinishInputView(f: Boolean) { super.onFinishInputView(f); lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE) }

    private fun getCurrentWord(): String = predictor.getCurrentWord(currentInputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: "")

    private fun performBackspace() {
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ic.deleteSurroundingTextInCodePoints(1, 0)
        else ic.deleteSurroundingText(1, 0)
    }

    private fun sendKey(code: Int, meta: Int = 0) {
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, code, 0, meta))
        currentInputConnection?.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, code, 0, meta))
    }

    private fun pasteImage(item: ClipboardItem) {
        val uri = item.imageUri ?: return
        val ic = currentInputConnection ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try { ic.commitContent(InputContentInfo(uri, android.content.ClipDescription("image", arrayOf(item.mimeType)), null), InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null) } catch (_: Exception) { item.text?.let { ic.commitText(it, 1) } }
        } else item.text?.let { ic.commitText(it, 1) }
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        val view = ComposeView(this)
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)

        view.setContent {
            val themePref by prefs.themePreference.collectAsState(initial = "system")
            val isSysDark = isSystemInDarkTheme()
            val useDark = when (themePref) { "light" -> false; "dark", "amoled" -> true; else -> isSysDark }

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
                val bp by prefs.bottomPadding.collectAsState(initial = 0)
                val nr by prefs.numberRowEnabled.collectAsState(initial = false)
                val ac by prefs.autoCapitalize.collectAsState(initial = true)
                val sc2 by prefs.spacebarCursor.collectAsState(initial = true)
                val lk by prefs.largeKeys.collectAsState(initial = false)
                
                val clipboardEnabled by prefs.clipboardEnabled.collectAsState(initial = true)
                val searchEnabled by prefs.searchEnabled.collectAsState(initial = true)
                val currencyEnabled by prefs.currencyEnabled.collectAsState(initial = true)
                val kaomojiEnabled by prefs.kaomojiEnabled.collectAsState(initial = true)
                val phrasesEnabled by prefs.phrasesEnabled.collectAsState(initial = true)

                val multiEnabled by prefs.multilingualEnabled.collectAsState(initial = false)
                val activeDicts by prefs.activeDicts.collectAsState(initial = setOf("en_us"))
                val dictId by prefs.dictionaryId.collectAsState(initial = "en_us")

                LaunchedEffect(clipboardEnabled) {
                    if (clipboardEnabled && clipboard == null) { clipboard = ClipboardHistory(this@HandBoardService); clipboard?.initialize() }
                    else if (!clipboardEnabled) { clipboard?.destroy(); clipboard = null }
                }

                LaunchedEffect(multiEnabled, activeDicts, dictId) {
                    withContext(Dispatchers.IO) { predictor.loadDictionaries(this@HandBoardService, if (multiEnabled) activeDicts else setOf(dictId)) }
                }

                val ls = remember { LayoutSwitcher(ln) }
                LaunchedEffect(ln) { ls.setLayout(ln) }

                val sugs = remember { mutableStateListOf<String>() }
                val showPred = pe && !isPasswordField && !isNumberField

                fun updateSuggestions() { sugs.clear(); if (!showPred) return; sugs.addAll(predictor.predict(currentInputConnection?.getTextBeforeCursor(100, 0)?.toString() ?: "", sc)) }

                Column {
                    KeyboardWrapper(widthFraction = wp / 100f, alignment = al) {
                        KeyboardView(
                            layoutSwitcher = ls, preferencesManager = prefs, heightScale = if (lk) hs * 1.25f else hs,
                            hapticEnabled = hap, soundEnabled = snd, numberRowEnabled = nr, spacebarCursor = sc2,
                            clipboardEnabled = clipboardEnabled, searchEnabled = searchEnabled, currencyEnabled = currencyEnabled,
                            kaomojiEnabled = kaomojiEnabled, phrasesEnabled = phrasesEnabled,
                            clipboardHistory = if (clipboardEnabled) clipboard else null,
                            suggestionBar = if (showPred) { { SuggestionBar(suggestions = sugs, onSuggestionClick = { 
                                val cur = getCurrentWord()
                                if (cur.isNotEmpty()) currentInputConnection?.deleteSurroundingText(cur.length, 0)
                                currentInputConnection?.commitText("$it ", 1)
                                predictor.onWordCommitted(it)
                                updateSuggestions()
                            }) } } else null,
                            onTextInput = { text ->
                                val ic = currentInputConnection
                                if (ic != null) {
                                    if (text == " ") {
                                        val now = System.currentTimeMillis()
                                        if (now - lastSpaceTime < 400 && !isPasswordField) {
                                            ic.deleteSurroundingText(1, 0); ic.commitText(". ", 1); lastSpaceTime = 0L; updateSuggestions(); return@KeyboardView
                                        }
                                        lastSpaceTime = now
                                    } else lastSpaceTime = 0L

                                    val final = if (text.length == 1 && text[0].isLetter() && ac && !isPasswordField) {
                                        val b = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""
                                        if (b.isEmpty() || b.trimEnd().lastOrNull() in listOf('.', '!', '?', '\n')) text.uppercase() else text
                                    } else text
                                    ic.commitText(final, 1)

                                    if (text == " ") { val w = getCurrentWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w) }
                                    updateSuggestions()
                                }
                            },
                            onBackspace = { performBackspace(); updateSuggestions() },
                            onEnter = { val w = getCurrentWord(); if (w.isNotEmpty()) predictor.onWordCommitted(w); sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER); sugs.clear() },
                            onEmojiInput = { currentInputConnection?.commitText(it, 1) },
                            onCursorMove = { val c = if (it > 0) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT; sendKey(c) },
                            onCursorHome = { sendKey(KeyEvent.KEYCODE_MOVE_HOME) }, onCursorEnd = { sendKey(KeyEvent.KEYCODE_MOVE_END) },
                            onSelectAll = { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) }, onCopy = { currentInputConnection?.performContextMenuAction(android.R.id.copy) }, onCut = { currentInputConnection?.performContextMenuAction(android.R.id.cut) }, onPaste = { currentInputConnection?.performContextMenuAction(android.R.id.paste) },
                            onUndo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON) }, onRedo = { sendKey(KeyEvent.KEYCODE_Z, KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON) },
                            onPasteImage = { pasteImage(it) },
                            onDismissKeyboard = { requestHideSelf(0) },
                            onOpenSettings = {
                                val intent = Intent(this@HandBoardService, MainActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                startActivity(intent)
                                requestHideSelf(0)
                            }
                        )
                    }
                    if (bp > 0) Spacer(Modifier.fillMaxWidth().height(bp.dp).background(KeyboardBackground))
                }
            }
        }
        return view
    }

    override fun onDestroy() { 
        clipboard?.destroy()
        clipboard = null
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        super.onDestroy() 
    }
}
