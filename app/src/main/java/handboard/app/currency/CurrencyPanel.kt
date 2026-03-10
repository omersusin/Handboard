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
import handboard.app.core.theme.*
import handboard.app.layout.ui.ContentCopyIcon
import handboard.app.layout.ui.RefreshIcon
import handboard.app.layout.ui.SwapHorizIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun CurrencyPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onResultCommit: (String) -> Unit,
    onClose: () -> Unit,
    maxHeight: Int = 260
) {
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("TRY") }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }

    val currencies = listOf(
        CurrencyInfo("TRY", "Türk Lirası", "₺"), CurrencyInfo("USD", "ABD Doları", "$"),
        CurrencyInfo("EUR", "Euro", "€"), CurrencyInfo("GBP", "İngiliz Sterlini", "£"),
        CurrencyInfo("JPY", "Japon Yeni", "¥"), CurrencyInfo("CHF", "İsviçre", "Fr"),
        CurrencyInfo("CAD", "Kanada", "C$"), CurrencyInfo("AUD", "Avustralya", "A$")
    )

    fun fetchRates() {
        isLoading = true
        // Doğrudan API çağrısı
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            val res = CurrencyApi.fetchRates(from)
            withContext(Dispatchers.Main) {
                if (res != null) rates = res
                isLoading = false
            }
        }
    }

    LaunchedEffect(from) { fetchRates() }

    val amountStr = query.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
    val amount = amountStr.toDoubleOrNull() ?: 1.0

    val resultText = remember(amount, from, to, rates) {
        val fromRate = rates[from]
        val toRate = rates[to]
        if (fromRate != null && toRate != null && fromRate != 0.0) {
            val res = amount * (toRate / fromRate)
            String.format(Locale.US, "%.2f", res)
        } else null
    }

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight.dp).background(KeyboardBackground).padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("💱 Currency Converter", color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { fetchRates() }.padding(8.dp)) { RefreshIcon(tint = KeyText, size = 14.dp) }
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { onClose() }.padding(8.dp)) { Text("✕", color = KeyText, fontSize = 12.sp) }
            }
        }

        if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth().height(2.dp), color = ShiftActiveBackground)

        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(KeyBackground).padding(10.dp)) {
            if (query.isEmpty()) Text("Type amount (e.g. 100)...", color = KeyTextDim, fontSize = 14.sp)
            else Row(verticalAlignment = Alignment.CenterVertically) {
                Text(query, color = KeyText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.padding(start = 2.dp).width(2.dp).height(18.dp).background(ShiftActiveBackground))
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            CurrencyChipRow(selected = from, items = currencies, onSelect = { from = it }, modifier = Modifier.weight(1f))
            Box(modifier = Modifier.padding(horizontal=4.dp).clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).clickable { val tmp = from; from = to; to = tmp }.padding(8.dp)) { SwapHorizIcon(tint = KeyText, size = 16.dp) }
            CurrencyChipRow(selected = to, items = currencies, onSelect = { to = it }, modifier = Modifier.weight(1f))
        }

        resultText?.let { resText ->
            val toSym = currencies.find { it.code == to }?.symbol ?: ""
            val fromSym = currencies.find { it.code == from }?.symbol ?: ""
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(ActionKeyBackground).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("$amount $fromSym", fontSize = 12.sp, color = KeyTextDim)
                    Text("$resText $toSym", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = KeyText)
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ShiftActiveBackground).clickable { onResultCommit("$resText $toSym"); onQueryChange("") }.padding(horizontal=12.dp, vertical=8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ContentCopyIcon(tint = KeyText, size = 14.dp)
                        Spacer(Modifier.width(6.dp))
                        Text("Paste", color = KeyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
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

data class CurrencyInfo(val code: String, val name: String, val symbol: String)
