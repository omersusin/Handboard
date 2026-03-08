package handboard.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "handboard_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_HEIGHT = floatPreferencesKey("keyboard_height")
        private val KEY_WIDTH = intPreferencesKey("keyboard_width")
        private val KEY_ALIGNMENT = intPreferencesKey("keyboard_alignment")
        private val KEY_LAYOUT = stringPreferencesKey("selected_layout")
        private val KEY_HAPTIC = booleanPreferencesKey("haptic_enabled")
    }

    val keyboardHeight: Flow<Float> = context.dataStore.data.map { it[KEY_HEIGHT] ?: 1.0f }
    val keyboardWidth: Flow<Int> = context.dataStore.data.map { it[KEY_WIDTH] ?: 100 }
    val keyboardAlignment: Flow<Int> = context.dataStore.data.map { it[KEY_ALIGNMENT] ?: 1 }
    val selectedLayout: Flow<String> = context.dataStore.data.map { it[KEY_LAYOUT] ?: "QWERTY" }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC] ?: true }

    suspend fun setKeyboardHeight(value: Float) {
        context.dataStore.edit { it[KEY_HEIGHT] = value }
    }

    suspend fun setKeyboardWidth(value: Int) {
        context.dataStore.edit { it[KEY_WIDTH] = value }
    }

    suspend fun setKeyboardAlignment(value: Int) {
        context.dataStore.edit { it[KEY_ALIGNMENT] = value }
    }

    suspend fun setSelectedLayout(value: String) {
        context.dataStore.edit { it[KEY_LAYOUT] = value }
    }

    suspend fun setHapticEnabled(value: Boolean) {
        context.dataStore.edit { it[KEY_HAPTIC] = value }
    }
}
