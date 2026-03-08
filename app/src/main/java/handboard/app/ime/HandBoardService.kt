package handboard.app.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import handboard.app.settings.PreferencesManager

class HandBoardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private lateinit var preferencesManager: PreferencesManager

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

            val layoutSwitcher = remember { LayoutSwitcher(layoutName) }
            LaunchedEffect(layoutName) {
                layoutSwitcher.setLayout(layoutName)
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
                        onTextInput = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onBackspace = {
                            currentInputConnection?.deleteSurroundingText(1, 0)
                        },
                        onEnter = {
                            sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_ENTER)
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }
}
