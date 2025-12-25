package io.github.rlimapro.ondeestacionei.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val SHOW_NOTE_DIALOG = booleanPreferencesKey("show_note_dialog")
    }

    val showNoteDialog: Flow<Boolean> = dataStore.data.map { it[SHOW_NOTE_DIALOG] ?: true }

    suspend fun setShowNoteDialog(enabled: Boolean) {
        dataStore.edit { it[SHOW_NOTE_DIALOG] = enabled }
    }
}