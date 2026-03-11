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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

fun InputMethodService.createComposeView(
    lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    content: @Composable () -> Unit
): View {
    // Ana Pencerenin (Window) kök görünümünü alıyoruz
    val decorView = window?.window?.decorView

    return ComposeView(this).apply {
        // Strateji: Klavye gizlendiğinde UI'ı yok et (Performans için)
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        // 1) Owner'ları doğrudan ComposeView'ın kendisine atıyoruz
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)

        // 2) Owner'ları Window'un DecorView'ına da atıyoruz (Popup, Dialog ve WindowRecomposer'ın çökmemesi için ŞART)
        decorView?.let {
            it.setViewTreeLifecycleOwner(lifecycleOwner)
            it.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
            it.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        }

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Android Navigasyon çubuğu (alt çizgi) ile klavyenin çakışmasını önle
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(0, 0, 0, navBar.bottom)
            insets
        }

        setContent { content() }
    }
}
