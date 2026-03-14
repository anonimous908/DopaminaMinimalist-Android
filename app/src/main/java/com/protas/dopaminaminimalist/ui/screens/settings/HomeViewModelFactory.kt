package com.protas.dopaminaminimalist.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.protas.dopaminaminimalist.data.repository.VicioRepository

class HomeViewModelFactory(private val repository: VicioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}