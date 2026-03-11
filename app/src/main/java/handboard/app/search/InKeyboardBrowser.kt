package handboard.app.search

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground
import handboard.app.layout.ui.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InKeyboardBrowser(
    url: String,
    onClose: () -> Unit,
    onCommitText: (String) -> Unit,
    onDismissKeyboard: () -> Unit, // ★ EKLENDİ (Klavyeyi Kapatmak için)
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(url) }
    var pageTitle by remember { mutableStateOf("Yükleniyor...") }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxWidth().fillMaxHeight().background(KeyboardBackground)) {
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // KAPAT TUŞU -> KLAVYEYİ KAPATIR
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { 
                webView?.destroy()
                onClose()
                onDismissKeyboard() 
            }.padding(8.dp)) { CloseIcon(tint = KeyText, size = 16.dp) }
            
            Spacer(Modifier.width(4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(canGoBack) ActionKeyBackground else KeyboardBackground).clickable(enabled = canGoBack) { webView?.goBack() }.padding(8.dp)) { BackArrowIcon(tint = if(canGoBack) KeyText else KeyTextDim, size = 16.dp) }
            
            Spacer(Modifier.width(4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if(canGoForward) ActionKeyBackground else KeyboardBackground).clickable(enabled = canGoForward) { webView?.goForward() }.padding(8.dp)) { ForwardArrowIcon(tint = if(canGoForward) KeyText else KeyTextDim, size = 16.dp) }
            
            Spacer(Modifier.width(4.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { webView?.reload() }.padding(8.dp)) { RefreshIcon(tint = KeyText, size = 14.dp) }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(pageTitle, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = KeyText)
                Text(currentUrl.removePrefix("https://").removePrefix("www."), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = KeyTextDim)
            }

            // PAYLAŞ TUŞU -> KLAVYEYİ KAPATIR
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { 
                try {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, pageTitle)
                        putExtra(Intent.EXTRA_TEXT, "$pageTitle\n$currentUrl")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(Intent.createChooser(intent, "Paylaş").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: Exception) {}
                onDismissKeyboard()
            }.padding(8.dp)) { ShareIcon(tint = KeyText, size = 14.dp) }

            Spacer(Modifier.width(4.dp))

            // YAPIŞTIR TUŞU -> KLAVYEYİ KAPATIR
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ShiftActiveBackground).clickable { 
                onCommitText(currentUrl)
                onDismissKeyboard()
            }.padding(8.dp)) { ContentCopyIcon(tint = KeyText, size = 14.dp) }
        }

        AnimatedVisibility(visible = isLoading) { LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().height(2.dp), color = ShiftActiveBackground) }

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
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(v: WebView?, p: String?, f: Bitmap?) { isLoading = true; p?.let { currentUrl = it } }
                        override fun onPageFinished(v: WebView?, p: String?) {
                            isLoading = false; canGoBack = v?.canGoBack() ?: false; canGoForward = v?.canGoForward() ?: false
                            p?.let { currentUrl = it }; v?.title?.let { pageTitle = it }
                        }
                        override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                            val reqUrl = r?.url?.toString() ?: return false
                            if (reqUrl.startsWith("http")) return false
                            try {
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(reqUrl)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                onDismissKeyboard()
                            } catch (_: Exception) {}
                            return true
                        }
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
