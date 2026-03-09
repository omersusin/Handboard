package handboard.app.translate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TranslationLanguages {
    val items = linkedMapOf(
        // En sık kullanılan diller (En Üstte)
        "auto" to "Auto Detect",
        "tr" to "Turkish",
        "en" to "English",
        "de" to "German",
        "es" to "Spanish",
        "ar" to "Arabic",
        "zh-CN" to "Chinese",
        "ja" to "Japanese",
        "fr" to "French",
        "ru" to "Russian",
        "it" to "Italian",
        
        // Diğer tüm diller (Alfabetik)
        "af" to "Afrikaans",
        "sq" to "Albanian",
        "am" to "Amharic",
        "hy" to "Armenian",
        "as" to "Assamese",
        "az" to "Azerbaijani",
        "bm" to "Bambara",
        "ba" to "Bashkir",
        "eu" to "Basque",
        "be" to "Belarusian",
        "bn" to "Bengali",
        "bho" to "Bhojpuri",
        "bg" to "Bulgarian",
        "yue" to "Cantonese",
        "ca" to "Catalan",
        "ceb" to "Cebuano",
        "ny" to "Chichewa",
        "lzh" to "Chinese (Literary)",
        "zh-TW" to "Chinese (Traditional)",
        "cv" to "Chuvash",
        "co" to "Corsican",
        "hr" to "Croatian",
        "cs" to "Czech",
        "da" to "Danish",
        "prs" to "Dari",
        "dv" to "Dhivehi",
        "doi" to "Dogri",
        "mhr" to "Eastern Mari",
        "eo" to "Esperanto",
        "et" to "Estonian",
        "ee" to "Ewe",
        "fo" to "Faroese",
        "fj" to "Fijian",
        "tl" to "Filipino",
        "fi" to "Finnish",
        "fr-CA" to "French (Canadian)",
        "fy" to "Frisian",
        "gl" to "Galician",
        "ka" to "Georgian",
        "el" to "Greek",
        "gn" to "Guarani",
        "gu" to "Gujarati",
        "ha" to "Hausa",
        "haw" to "Hawaiian",
        "he" to "Hebrew",
        "mrj" to "Hill Mari",
        "hi" to "Hindi",
        "hu" to "Hungarian",
        "is" to "Icelandic",
        "ig" to "Igbo",
        "ilo" to "Ilocano",
        "id" to "Indonesian",
        "ikt" to "Inuinnaqtun",
        "iu" to "Inuktitut",
        "iu-Latn" to "Inuktitut (Latin)",
        "ga" to "Irish",
        "jv" to "Javanese",
        "kn" to "Kannada",
        "kk" to "Kazakh",
        "km" to "Khmer",
        "rw" to "Kinyarwanda",
        "tlh-Latn" to "Klingon",
        "gom" to "Konkani",
        "kri" to "Krio",
        "ckb" to "Kurdish (Central)",
        "ku" to "Kurdish (Northern)",
        "ky" to "Kyrgyz",
        "lo" to "Lao",
        "la" to "Latin",
        "lv" to "Latvian",
        "ln" to "Lingala",
        "lt" to "Lithuanian",
        "lg" to "Luganda",
        "lb" to "Luxembourgish",
        "mk" to "Macedonian",
        "mai" to "Maithili",
        "ms" to "Malay",
        "ml" to "Malayalam",
        "mi" to "Maori",
        "mr" to "Marathi",
        "lus" to "Mizo",
        "mn" to "Mongolian",
        "mn-Mong" to "Mongolian (Traditional)",
        "my" to "Myanmar",
        "ne" to "Nepali",
        "or" to "Odia",
        "om" to "Oromo",
        "pap" to "Papiamento",
        "ps" to "Pashto",
        "fa" to "Persian",
        "pl" to "Polish",
        "pt-BR" to "Portuguese (Brazilian)",
        "pt-PT" to "Portuguese (European)",
        "pa" to "Punjabi",
        "otq" to "Querétaro Otomi",
        "ro" to "Romanian",
        "sm" to "Samoan",
        "sa" to "Sanskrit",
        "gd" to "Scots Gaelic",
        "nso" to "Sepedi",
        "sr-Cyrl" to "Serbian (Cyrillic)",
        "sr-Latn" to "Serbian (Latin)",
        "st" to "Sesotho",
        "sn" to "Shona",
        "sd" to "Sindhi",
        "si" to "Sinhala",
        "sk" to "Slovak",
        "sl" to "Slovenian",
        "so" to "Somali",
        "su" to "Sundanese",
        "sw" to "Swahili",
        "sv" to "Swedish",
        "ty" to "Tahitian",
        "tg" to "Tajik",
        "ta" to "Tamil",
        "tt" to "Tatar",
        "te" to "Telugu",
        "th" to "Thai",
        "bo" to "Tibetan",
        "ti" to "Tigrinya",
        "to" to "Tongan",
        "ts" to "Tsonga",
        "tk" to "Turkmen",
        "tw" to "Twi",
        "udm" to "Udmurt",
        "uk" to "Ukrainian",
        "hsb" to "Upper Sorbian",
        "ur" to "Urdu",
        "ug" to "Uyghur",
        "uz" to "Uzbek",
        "vi" to "Vietnamese",
        "cy" to "Welsh",
        "xh" to "Xhosa",
        "sah" to "Yakut",
        "yi" to "Yiddish",
        "yo" to "Yoruba",
        "yua" to "Yucatec Maya",
        "zu" to "Zulu"
    )

    fun getLanguageName(code: String): String = items[code] ?: code
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
