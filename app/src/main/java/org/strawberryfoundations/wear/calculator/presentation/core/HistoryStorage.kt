package org.strawberryfoundations.wear.calculator.presentation.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val Context.historyDataStore by preferencesDataStore(name = "history")
private val historyKey = stringPreferencesKey("calculator_history")

private val historyJson = Json {
    ignoreUnknownKeys = true
}

suspend fun loadHistory(context: Context): List<HistoryEntry> {
    val raw = context.historyDataStore.data.first()[historyKey] ?: return emptyList()
    return runCatching {
        historyJson.decodeFromString<List<HistoryEntry>>(raw)
    }.getOrDefault(emptyList())
}

suspend fun saveHistory(context: Context, entries: List<HistoryEntry>) {
    val payload = historyJson.encodeToString(entries)
    context.historyDataStore.edit { prefs ->
        prefs[historyKey] = payload
    }
}
