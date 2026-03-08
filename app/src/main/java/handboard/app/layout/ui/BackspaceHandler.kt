package handboard.app.layout.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackspaceHandler(private val scope: CoroutineScope) {

    private var repeatJob: Job? = null

    fun startRepeating(onDelete: () -> Unit) {
        repeatJob?.cancel()
        onDelete() // immediate first delete
        repeatJob = scope.launch {
            delay(400) // initial delay
            while (true) {
                onDelete()
                delay(50) // repeat speed
            }
        }
    }

    fun stopRepeating() {
        repeatJob?.cancel()
        repeatJob = null
    }
}
