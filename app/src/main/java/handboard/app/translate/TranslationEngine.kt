package handboard.app.translate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TranslationLanguages {
    val items = linkedMapOf(
        "auto" to "Auto Detect",
        "tr" to "Turkish",
        "en" to "English",
        "de" to "German",
        "fr" to "French",
        "es" to "Spanish",
        "it" to "Italian",
        "ar" to "Arabic",
        "ru" to "Russian",
        "zh-CN" to "Chinese",
        "ja" to "Japanese"
    )
}

object TranslationEngine {
    private const val API_URL = "https://deep-translator-api.azurewebsites.net/google/"

    suspend fun translate(source: String, target: String, text: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 5000
                    readTimeout = 5000
                }

                val body = JSONObject().apply {
                    put("source", source)
                    put("target", target)
                    put("text", text)
                    put("proxies", JSONArray())
                }.toString()

                connection.outputStream.use {
                    it.write(body.toByteArray(Charsets.UTF_8))
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    Result.success(json.getString("translation"))
                } else {
                    Result.failure(Exception("HTTP ${connection.responseCode}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
