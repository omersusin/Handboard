package handboard.app.search

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import handboard.app.layout.ui.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InKeyboardBrowser(
    url: String,
    onClose: () -> Unit,
    onCommitText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconTint = MaterialTheme.colorScheme.onSurface
    val disabledTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

    var webView by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(url) }
    var pageTitle by remember { mutableStateOf("Yükleniyor…") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { webView?.destroy(); onClose() }, modifier = Modifier.size(32.dp)) { CloseIcon(tint = iconTint, size = 18.dp) }
                    IconButton(onClick = { webView?.goBack() }, enabled = canGoBack, modifier = Modifier.size(32.dp)) { BackArrowIcon(tint = if (canGoBack) iconTint else disabledTint, size = 18.dp) }
                    IconButton(onClick = { webView?.goForward() }, enabled = canGoForward, modifier = Modifier.size(32.dp)) { ForwardArrowIcon(tint = if (canGoForward) iconTint else disabledTint, size = 18.dp) }
                    IconButton(onClick = { webView?.reload() }, modifier = Modifier.size(32.dp)) { RefreshIcon(tint = iconTint, size = 16.dp) }

                    Column(modifier = Modifier.weight(1f).padding(horizontal = 6.dp)) {
                        Text(pageTitle, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                        Text(currentUrl.removePrefix("https://").removePrefix("www."), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("URL", currentUrl))
                        },
                        modifier = Modifier.size(32.dp)
                    ) { ContentCopyIcon(tint = iconTint, size = 16.dp) }

                    IconButton(onClick = { onCommitText(currentUrl) }, modifier = Modifier.size(32.dp)) { ShareIcon(tint = iconTint, size = 16.dp) }
                }

                AnimatedVisibility(visible = isLoading) {
                    LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(2.dp))
                }
            }
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        allowContentAccess = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, pageUrl: String?, favicon: Bitmap?) { isLoading = true; pageUrl?.let { currentUrl = it } }
                        override fun onPageFinished(view: WebView?, pageUrl: String?) {
                            isLoading = false
                            canGoBack = view?.canGoBack() ?: false
                            canGoForward = view?.canGoForward() ?: false
                            pageUrl?.let { currentUrl = it }
                            view?.title?.let { pageTitle = it }
                        }
                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress
                            if (newProgress == 100) isLoading = false
                        }
                        override fun onReceivedTitle(view: WebView?, title: String?) { title?.let { pageTitle = it } }
                    }
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}
