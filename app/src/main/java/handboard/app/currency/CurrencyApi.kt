package handboard.app.currency

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object CurrencyApi {
    private const val TAG = "CurrencyApi"
    private const val BASE = "https://open.er-api.com/v6/latest/"

    suspend fun fetchRates(base: String): Map<String, Double>? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL("$BASE${base.uppercase()}").openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000; readTimeout = 4000
            }
            try {
                if (conn.responseCode != 200) return@withContext null
                val json = JSONObject(conn.inputStream.bufferedReader().use { it.readText() })
                if (json.optString("result") != "success") return@withContext null
                val rates = json.getJSONObject("rates")
                buildMap { rates.keys().forEach { put(it, rates.getDouble(it)) } }
            } finally { conn.disconnect() }
        } catch (e: Exception) { null }
    }
}
