package com.protas.dopaminaminimalist.ui.screens.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class HomeViewModel(private val repository: VicioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        ejecutarDiagnostico()
    }

    fun ejecutarDiagnostico() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val resultado = repository.obtenerAnalisisCompleto()
            val datosGrafica = repository.obtenerHistorialGrafica() // <--- ¡DATOS REALES!

            resultado
                .onSuccess { score ->
                    _uiState.update {
                        it.copy(
                            scoreVicio = score,
                            historyData = datosGrafica, // <--- Aquí llenamos la gráfica
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}