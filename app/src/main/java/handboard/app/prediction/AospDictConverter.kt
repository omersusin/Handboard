package handboard.app.prediction

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

class AospDictConverter(private val context: Context) {

    companion object {
        private const val TAG = "AospDictConverter"
        private const val OUTPUT_DIR = "dictionaries"
        private const val DEFAULT_FREQ = 500
        private const val MIN_WORD_LEN = 2
        private const val MAX_WORD_LEN = 30
        private const val SAMPLE_SIZE = 4096
    }

    suspend fun convertAndSave(uri: Uri): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val rawBytes = readBytes(uri)
            Log.i(TAG, "Dosya okundu: ${rawBytes.size} byte")

            val outputFile = prepareOutputFile()

            if (isPlainText(rawBytes)) {
                Log.i(TAG, "Düz metin algılandı -> doğrudan kopyalanıyor")
                outputFile.writeBytes(rawBytes)
            } else {
                Log.i(TAG, "Binary algılandı -> Scavenger taraması başlıyor")
                val words = scavengeWords(rawBytes)

                if (words.isEmpty()) {
                    throw IllegalStateException("Geçerli kelime bulunamadı. Bozuk dosya olabilir.")
                }
                writeOutput(words, outputFile)
            }
            outputFile
        }
    }

    private fun isPlainText(bytes: ByteArray): Boolean {
        if (bytes.isEmpty()) return false
        val checkLen = minOf(bytes.size, SAMPLE_SIZE)
        var textChars = 0

        for (i in 0 until checkLen) {
            val b = bytes[i].toInt() and 0xFF
            when {
                b == 0x00 -> return false
                b == 0x09 || b == 0x0A || b == 0x0D -> textChars++
                b in 0x20..0x7E -> textChars++
                b in 0xC0..0xFD -> textChars++
                b in 0x80..0xBF -> textChars++
            }
        }
        return (textChars.toFloat() / checkLen) > 0.90f
    }

    private suspend fun scavengeWords(bytes: ByteArray): List<String> {
        val wordSet = LinkedHashSet<String>(4096)
        val buffer = StringBuilder(MAX_WORD_LEN + 1)
        var pos = 0

        while (pos < bytes.size) {
            if (pos % 10_000 == 0) {
                coroutineContext.ensureActive()
            }

            val decoded = decodeUtf8Char(bytes, pos)
            if (decoded != null) {
                val (codePoint, byteLen) = decoded
                if (Character.isLetter(codePoint)) {
                    if (buffer.length < MAX_WORD_LEN) buffer.appendCodePoint(codePoint)
                    pos += byteLen
                    continue
                }
            }
            flushBuffer(buffer, wordSet)
            pos++
        }
        flushBuffer(buffer, wordSet)
        return wordSet.toList()
    }

    private fun flushBuffer(buffer: StringBuilder, target: MutableSet<String>) {
        if (buffer.length in MIN_WORD_LEN..MAX_WORD_LEN) {
            val candidate = buffer.toString()
            if (candidate.all { Character.isLetter(it.code) }) target.add(candidate)
        }
        buffer.clear()
    }

    private fun decodeUtf8Char(bytes: ByteArray, offset: Int): Pair<Int, Int>? {
        if (offset >= bytes.size) return null
        val b0 = bytes[offset].toInt() and 0xFF
        if (b0 < 0x80) return Pair(b0, 1)

        val (expectedLen, initialMask) = when {
            b0 in 0xC2..0xDF -> Pair(2, 0x1F)
            b0 in 0xE0..0xEF -> Pair(3, 0x0F)
            b0 in 0xF0..0xF4 -> Pair(4, 0x07)
            else -> return null
        }

        if (offset + expectedLen > bytes.size) return null
        var codePoint = b0 and initialMask

        for (i in 1 until expectedLen) {
            val bi = bytes[offset + i].toInt() and 0xFF
            if (bi and 0xC0 != 0x80) return null
            codePoint = (codePoint shl 6) or (bi and 0x3F)
        }

        val minCodePoint = when (expectedLen) { 2 -> 0x80; 3 -> 0x800; 4 -> 0x10000; else -> return null }
        if (codePoint < minCodePoint || codePoint > 0x10FFFF || codePoint in 0xD800..0xDFFF) return null

        return Pair(codePoint, expectedLen)
    }

    private fun readBytes(uri: Uri): ByteArray {
        return context.contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() }
            ?: throw IllegalStateException("Dosya açılamadı: $uri")
    }

    private fun prepareOutputFile(): File {
        val dir = File(context.filesDir, OUTPUT_DIR).apply { mkdirs() }
        return File(dir, "custom_${System.currentTimeMillis()}.txt")
    }

    private fun writeOutput(words: List<String>, file: File) {
        file.bufferedWriter(Charsets.UTF_8).use { writer ->
            for (word in words) {
                writer.write(word)
                writer.write("\t")
                writer.write(DEFAULT_FREQ.toString())
                writer.newLine()
            }
            writer.flush()
        }
    }
}
