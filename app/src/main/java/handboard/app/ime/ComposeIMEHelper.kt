package handboard.app.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

fun InputMethodService.createComposeView(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    content: @Composable () -> Unit
): View {
    // Set lifecycle on the IME window's decor view so Compose can find it
    val decorView = window?.window?.decorView
    decorView?.let {
        it.setViewTreeLifecycleOwner(lifecycleOwner)
        it.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    }

    return ComposeView(this).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent { content() }
    }
}
