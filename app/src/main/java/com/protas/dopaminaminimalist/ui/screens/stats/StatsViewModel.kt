package com.protas.dopaminaminimalist.ui.screens.stats


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.dopaminaminimalist.data.repository.DatosCompletos
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado de la UI específico para esta pantalla
data class StatsUiState(
    val isLoading: Boolean = true,
    val datos: DatosCompletos? = null,
    val error: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: VicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        cargarEstadisticas()
    }

    private fun cargarEstadisticas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Usamos el repositorio que ya refactorizamos
                val datosCompletos = repository.obtenerTodosDatos()
                _uiState.update {
                    it.copy(isLoading = false, datos = datosCompletos)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.localizedMessage)
                }
            }
        }
    }
}