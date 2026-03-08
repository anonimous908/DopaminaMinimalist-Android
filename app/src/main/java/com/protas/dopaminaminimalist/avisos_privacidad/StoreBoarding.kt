package com.protas.dopaminaminimalist.avisos_privacidad

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore


class StoreBoarding(private val context: Context) {

    // Creamos la "base de datos" pequeña
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        val STORE_BOARDING_KEY = booleanPreferencesKey("on_boarding_completed")
    }




    // Función para guardar que YA lo vimos
    suspend fun saveBoarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[STORE_BOARDING_KEY] = completed
        }
    }
}