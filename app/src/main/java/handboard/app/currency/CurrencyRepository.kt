package handboard.app.currency

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CurrencyInfo(val code: String, val name: String, val symbol: String)

class CurrencyRepository {
    val currencies = listOf(
        CurrencyInfo("TRY", "Türk Lirası", "₺"), CurrencyInfo("USD", "ABD Doları", "$"),
        CurrencyInfo("EUR", "Euro", "€"), CurrencyInfo("GBP", "İngiliz Sterlini", "£"),
        CurrencyInfo("JPY", "Japon Yeni", "¥"), CurrencyInfo("CHF", "İsviçre Frangı", "Fr"),
        CurrencyInfo("CAD", "Kanada Doları", "C$"), CurrencyInfo("AUD", "Avustralya Doları", "A$"),
        CurrencyInfo("CNY", "Çin Yuanı", "¥"), CurrencyInfo("SAR", "Suudi Riyali", "﷼"),
        CurrencyInfo("RUB", "Rus Rublesi", "₽"), CurrencyInfo("KRW", "Kore Wonu", "₩")
    )

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val cache = mutableMapOf<String, Map<String, Double>>()

    suspend fun loadRates(base: String) {
        cache[base]?.let { _rates.value = it; return }
        _isLoading.value = true; _error.value = null
        val result = CurrencyApi.fetchRates(base)
        if (result != null) { _rates.value = result; cache[base] = result }
        else _error.value = "Failed to load rates"
        _isLoading.value = false
    }

    fun convert(amount: Double, from: String, to: String): Double? {
        val r = _rates.value
        val fromRate = r[from] ?: return null
        val toRate = r[to] ?: return null
        return amount * (toRate / fromRate)
    }
}
