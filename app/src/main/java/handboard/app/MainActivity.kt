package handboard.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import handboard.app.core.theme.HandBoardTheme
import handboard.app.settings.PreferencesManager
import handboard.app.settings.SettingsScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferencesManager = PreferencesManager(this)

        setContent {
            HandBoardTheme {
                var showSettings by rememberSaveable { mutableStateOf(false) }

                if (showSettings) {
                    SettingsScreen(
                        preferencesManager = preferencesManager,
                        onBack = { showSettings = false }
                    )
                } else {
                    // Poll keyboard status
                    var isEnabled by rememberSaveable { mutableStateOf(false) }
                    var isSelected by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            isEnabled = isKeyboardEnabled()
                            isSelected = isKeyboardSelected()
                            delay(1000)
                        }
                    }

                    SetupScreen(
                        isEnabled = isEnabled,
                        isSelected = isSelected,
                        onEnableKeyboard = {
                            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                        },
                        onSelectKeyboard = {
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showInputMethodPicker()
                        },
                        onOpenSettings = { showSettings = true }
                    )
                }
            }
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.enabledInputMethodList.any { it.packageName == packageName }
    }

    private fun isKeyboardSelected(): Boolean {
        val currentIme = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        return currentIme?.startsWith("$packageName/") == true
    }
}

@Composable
fun SetupScreen(
    isEnabled: Boolean,
    isSelected: Boolean,
    onEnableKeyboard: () -> Unit,
    onSelectKeyboard: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(48.dp))

            SetupStep(
                number = "1",
                title = stringResource(R.string.step1_title),
                description = stringResource(R.string.step1_desc),
                buttonText = stringResource(R.string.step1_button),
                status = if (isEnabled) stringResource(R.string.status_enabled) else stringResource(R.string.status_not_enabled),
                isDone = isEnabled,
                onClick = onEnableKeyboard
            )
            Spacer(Modifier.height(16.dp))
            SetupStep(
                number = "2",
                title = stringResource(R.string.step2_title),
                description = stringResource(R.string.step2_desc),
                buttonText = stringResource(R.string.step2_button),
                status = if (isSelected) stringResource(R.string.status_selected) else stringResource(R.string.status_not_selected),
                isDone = isSelected,
                onClick = onSelectKeyboard
            )
            Spacer(Modifier.height(32.dp))
            OutlinedButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.keyboard_settings))
            }
        }
    }
}

@Composable
fun SetupStep(
    number: String,
    title: String,
    description: String,
    buttonText: String,
    status: String,
    isDone: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isDone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = if (isDone) "✓" else number,
                            color = if (isDone) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDone) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick) { Text(buttonText) }
        }
    }
}
