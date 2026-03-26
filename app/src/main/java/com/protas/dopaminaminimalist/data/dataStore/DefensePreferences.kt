package com.protas.dopaminaminimalist.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "defense_prefs")

class DefensePreferences(private val context: Context) {
    companion object {
        // Definir una llave por cada uno de tus 8 toggles
        val NOTIFY_KEY = booleanPreferencesKey("notify_active")
        val BARRIER_KEY = booleanPreferencesKey("barrier_active")
        val GRAYSCALE_KEY = booleanPreferencesKey("grayscale_active")
        val MONK_KEY = booleanPreferencesKey("monk_active")
        val AGGRESSIVE_KEY = booleanPreferencesKey("aggressive_active")
        val STATS_KEY = booleanPreferencesKey("stats_active")
        val NIGHT_KEY = booleanPreferencesKey("night_active")
        val KILL_KEY = booleanPreferencesKey("kill_active")
    }

    // Leer todas las preferencias como un mapa o clase de datos
    val getSettings: Flow<Map<String, Boolean>> = context.dataStore.data
        .map { prefs ->
            mapOf(
                "notify" to (prefs[NOTIFY_KEY] ?: false),
                "barrier" to (prefs[BARRIER_KEY] ?: false),
                "grayscale" to (prefs[GRAYSCALE_KEY] ?: false),
                "monk" to (prefs[MONK_KEY] ?: false),
                "aggressive" to (prefs[AGGRESSIVE_KEY] ?: false),
                "stats" to (prefs[STATS_KEY] ?: false),
                "night" to (prefs[NIGHT_KEY] ?: false),
                "kill" to (prefs[KILL_KEY] ?: false)
            )
        }

    // Función genérica para guardar cambios
    suspend fun saveSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { it[key] = value }
    }
}