package handboard.app.currency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.KeyboardBackground
import handboard.app.core.theme.ShiftActiveBackground
import java.util.Locale

@Composable
fun CurrencyPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onResultCommit: (String) -> Unit,
    onClose: () -> Unit,
    maxHeight: Int = 260
) {
    val repo = remember { CurrencyRepository() }
    val rates by repo.rates.collectAsState()
    val isLoading by repo.isLoading.collectAsState()
    val error by repo.error.collectAsState()
    val currencies = repo.currencies

    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("TRY") }
    var result by remember { mutableStateOf<String?>(null) }

    val amountStr = query.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
    val amount = amountStr.toDoubleOrNull() ?: 1.0

    LaunchedEffect(Unit) { repo.loadRates("USD") }
    
    DisposableEffect(Unit) {
        onDispose { repo.destroy() }
    }

    LaunchedEffect(amount, from, to, rates) {
        try {
            if (rates.isNotEmpty()) {
                val converted = repo.convert(amount, from, to)
                result = converted?.let { String.format(Locale.US, "%,.2f", it) }
            } else result = null
        } catch (_: Exception) { result = null }
    }

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight.dp).background(KeyboardBackground).padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("💱 Currency Converter", color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { repo.loadRates(from) }.padding(8.dp)) {
                    Text("🔄", color = KeyText, fontSize = 14.sp)
                }
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(8.dp)) {
                    Text("✕", color = KeyText, fontSize = 12.sp)
                }
            }
        }

        if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth().height(2.dp), color = ShiftActiveBackground)
        error?.let { Text("⚠️ $it", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }

        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(KeyBackground).padding(10.dp)) {
            if (query.isEmpty()) Text("Type amount (e.g. 100)...", color = KeyTextDim, fontSize = 14.sp)
            else Row(verticalAlignment = Alignment.CenterVertically) {
                Text(query, color = KeyText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.padding(start = 2.dp).width(2.dp).height(18.dp).background(ShiftActiveBackground))
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            CurrencyChipRow(selected = from, items = currencies, onSelect = { from = it; repo.loadRates(it) }, modifier = Modifier.weight(1f))
            Box(modifier = Modifier.padding(horizontal=4.dp).clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { val tmp = from; from = to; to = tmp; repo.loadRates(from) }.padding(8.dp)) { 
                Text("⇄", color = KeyText, fontSize = 16.sp) 
            }
            CurrencyChipRow(selected = to, items = currencies, onSelect = { to = it }, modifier = Modifier.weight(1f))
        }

        result?.let { resText ->
            val toSym = currencies.find { it.code == to }?.symbol ?: ""
            val fromSym = currencies.find { it.code == from }?.symbol ?: ""
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("$amount $fromSym", fontSize = 12.sp, color = KeyTextDim)
                    Text("$resText $toSym", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KeyText)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ShiftActiveBackground).clickable { onResultCommit("$resText $toSym"); onQueryChange("") }.padding(horizontal=12.dp, vertical=8.dp)) {
                    Text("📋 Paste", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CurrencyChipRow(selected: String, items: List<CurrencyInfo>, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyRow(modifier.background(KeyBackground, RoundedCornerShape(8.dp)).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(items) { info ->
            val isSelected = info.code == selected
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (isSelected) ShiftActiveBackground else ActionKeyBackground).clickable { onSelect(info.code) }.padding(horizontal=8.dp, vertical=6.dp)) {
                Text("${info.symbol} ${info.code}", color = KeyText, fontSize = 12.sp, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}
