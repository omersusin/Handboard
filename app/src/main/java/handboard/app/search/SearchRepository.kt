package handboard.app.search

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchRepository {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val cache = LinkedHashMap<String, List<String>>(40, 0.75f, true)
    private var debounceJob: Job? = null

    fun onQueryChanged(query: String) {
        debounceJob?.cancel()
        val trimmed = query.trim()

        if (trimmed.isBlank()) {
            _suggestions.value = emptyList()
            _isLoading.value = false
            return
        }

        debounceJob = scope.launch {
            delay(320) // debounce
            
            val key = trimmed.lowercase()
            val cached = cache[key]
            if (cached != null) {
                _suggestions.value = cached
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = true

            try {
                val results = GoogleSuggestApi.fetch(trimmed)
                if (isActive) {
                    _suggestions.value = results
                    if (results.isNotEmpty()) {
                        if (cache.size > 40) cache.remove(cache.keys.first())
                        cache[key] = results
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isActive) _suggestions.value = emptyList()
            } finally {
                if (isActive) _isLoading.value = false
            }
        }
    }

    fun clear() {
        debounceJob?.cancel()
        _suggestions.value = emptyList()
        _isLoading.value = false
    }

    fun destroy() {
        job.cancel()
    }
}
