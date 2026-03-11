package handboard.app.currency

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CurrencyRepository {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val cache = mutableMapOf<String, Map<String, Double>>()
    
    // YENİ: Dinamik oluşturulacak olan para birimi listesi
    var availableCurrencies = listOf(
        CurrencyInfo("TRY", "Türk Lirası", "₺"), CurrencyInfo("USD", "ABD Doları", "$"),
        CurrencyInfo("EUR", "Euro", "€"), CurrencyInfo("GBP", "İngiliz Sterlini", "£")
    )

    fun loadRates(base: String) {
        cache[base]?.let { 
            _rates.value = it
            _error.value = null
            return 
        }

        scope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = fetchRatesFromApi(base)
                if (isActive) {
                    if (result != null && result.isNotEmpty()) {
                        _rates.value = result
                        cache[base] = result
                        _error.value = null
                        
                        // Dinamik listeyi inşa et
                        val newList = mutableListOf<CurrencyInfo>()
                        val priority = listOf("TRY", "USD", "EUR", "GBP")
                        result.keys.forEach { code -> 
                            newList.add(CurrencyInfo(code, code, code.take(2)))
                        }
                        val sorted = newList.sortedBy { it.code }
                        val top = priority.mapNotNull { p -> sorted.find { it.code == p } }
                        availableCurrencies = top + sorted.filter { it.code !in priority }
                    } else {
                        _error.value = "Failed to fetch rates"
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isActive) _error.value = "Connection error"
            } finally {
                if (isActive) _isLoading.value = false
            }
        }
    }

    private fun fetchRatesFromApi(base: String): Map<String, Double>? {
        val url = URL("https://open.er-api.com/v6/latest/${base.uppercase()}")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        try {
            if (conn.responseCode != 200) return null
            val body = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val json = JSONObject(body)
            if (json.optString("result") != "success") return null

            val ratesObj = json.getJSONObject("rates")
            val map = HashMap<String, Double>()
            val keys = ratesObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                try { map[key] = ratesObj.getDouble(key) } catch (_: Exception) {}
            }
            return map
        } finally { conn.disconnect() }
    }

    fun convert(amount: Double, from: String, to: String): Double? {
        val r = _rates.value
        if (r.isEmpty()) return null
        val fromRate = r[from] ?: return null
        val toRate = r[to] ?: return null
        if (fromRate == 0.0) return null
        return amount * (toRate / fromRate)
    }

    fun destroy() { job.cancel() }
}

data class CurrencyInfo(val code: String, val name: String, val symbol: String)
