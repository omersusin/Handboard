package handboard.app.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

fun InputMethodService.createComposeView(
    lifecycleOwner: LifecycleOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    content: @Composable () -> Unit
): View {
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

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Handle bottom navigation bar insets
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(0, 0, 0, navBar.bottom)
            insets
        }

        setContent { content() }
    }
}
