package com.protas.dopaminaminimalist.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import com.protas.dopaminaminimalist.data.dataStore.DefensePreferences // Importa tu nueva clase

class HomeViewModelFactory(
    private val repository: VicioRepository,
    private val defensePrefs: DefensePreferences // 1. Agrega el parámetro aquí
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // 2. Pasa ambos parámetros al constructor
            return HomeViewModel(repository, defensePrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}