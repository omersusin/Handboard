package handboard.app.search

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object GoogleSuggestApi {
    private const val TAG = "GoogleSuggestApi"
    private const val BASE = "https://suggestqueries.google.com/complete/search?client=firefox&hl=tr&q="

    suspend fun fetch(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val url = URL("$BASE${URLEncoder.encode(query.trim(), "UTF-8")}")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 3000
                readTimeout = 3000
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            try {
                if (conn.responseCode != 200) return@withContext emptyList()
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val arr = JSONArray(body)
                if (arr.length() < 2) return@withContext emptyList()
                val suggestions = arr.getJSONArray(1)
                val results = mutableListOf<String>()
                for (i in 0 until suggestions.length()) {
                    val s = suggestions.optString(i)
                    if (!s.isNullOrBlank()) results.add(s)
                }
                results
            } finally { conn.disconnect() }
        } catch (e: Exception) {
            Log.e(TAG, "Suggest hatası: ${e.message}")
            emptyList()
        }
    }
}
