package com.protas.dopaminaminimalist.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extensión para inicializar el DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

class OnBoardingPreferences(private val context: Context) {

    companion object {
        // La llave con la que buscaremos el dato en el disco
        val ON_BOARDING_COMPLETED_KEY = booleanPreferencesKey("on_boarding_completed")
    }

    /**
     * LECTURA: Esta es la parte que le falta a tu código.
     * Expone un Flow que la MainActivity puede observar (collectAsState).
     */
    val getBoarding: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Si el valor no existe todavía, devolvemos 'false' por defecto
            preferences[ON_BOARDING_COMPLETED_KEY] ?: false
        }

    /**
     * ESCRITURA: Guarda el estado (true/false)
     */
    suspend fun saveBoarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ON_BOARDING_COMPLETED_KEY] = completed
        }
    }
}