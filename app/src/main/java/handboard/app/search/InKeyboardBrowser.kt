package handboard.app.search

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InKeyboardBrowser(
    url: String,
    onClose: () -> Unit,
    onCommitText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(url) }
    var pageTitle by remember { mutableStateOf("Loading...") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxWidth().fillMaxHeight().background(MaterialTheme.colorScheme.surface)) {
        Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { webView?.destroy(); onClose() }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Close, "Close", Modifier.size(18.dp)) }
                    IconButton(onClick = { webView?.goBack() }, enabled = canGoBack, modifier = Modifier.size(32.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", Modifier.size(18.dp)) }
                    IconButton(onClick = { webView?.goForward() }, enabled = canGoForward, modifier = Modifier.size(32.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Forward", Modifier.size(18.dp)) }
                    IconButton(onClick = { webView?.reload() }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Refresh, "Reload", Modifier.size(16.dp)) }

                    Column(modifier = Modifier.weight(1f).padding(horizontal = 6.dp)) {
                        Text(pageTitle, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                        Text(currentUrl.removePrefix("https://").removePrefix("www."), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(
                        onClick = {
                            val cb = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            cb.setPrimaryClip(android.content.ClipData.newPlainText("URL", currentUrl))
                        }, modifier = Modifier.size(32.dp)
                    ) { Icon(Icons.Default.ContentCopy, "Copy URL", Modifier.size(14.dp)) }

                    IconButton(onClick = { onCommitText(currentUrl) }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Share, "Paste", Modifier.size(14.dp)) }
                }
                AnimatedVisibility(visible = isLoading) { LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(2.dp)) }
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
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        userAgentString = settings.userAgentString.replace("wv", "")
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(v: WebView?, p: String?, f: Bitmap?) { isLoading = true; p?.let { currentUrl = it } }
                        override fun onPageFinished(v: WebView?, p: String?) {
                            isLoading = false; canGoBack = v?.canGoBack() ?: false; canGoForward = v?.canGoForward() ?: false
                            p?.let { currentUrl = it }; v?.title?.let { pageTitle = it }
                        }
                        override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean = false
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(v: WebView?, p: Int) { progress = p; if (p == 100) isLoading = false }
                        override fun onReceivedTitle(v: WebView?, t: String?) { t?.let { pageTitle = it } }
                    }
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}
