package com.protas.dopaminaminimalist.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protas.dopaminaminimalist.data.dataStore.DefensePreferences
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel // 1. Le dice a Hilt que este ViewModel usará el almacén
class HomeViewModel @Inject constructor( // 2. @Inject le pide a Hilt que traiga las herramientas
    private val repository: VicioRepository,
    private val defensePrefs: DefensePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // CORRECCIÓN: Convertimos el Flow en StateFlow de forma correcta
    val settingsState: StateFlow<Map<String, Boolean>> = defensePrefs.getSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Mantiene el flujo activo 5 seg tras cerrar la app
            initialValue = emptyMap()
        )

    fun toggleSetting(id: String, isEnabled: Boolean) {
        viewModelScope.launch {
            // Buscamos la llave correcta en DefensePreferences
            val key = when(id) {
                "notify"     -> DefensePreferences.NOTIFY_KEY
                "barrier"    -> DefensePreferences.BARRIER_KEY
                "grayscale"  -> DefensePreferences.GRAYSCALE_KEY
                "monk"       -> DefensePreferences.MONK_KEY
                "aggressive" -> DefensePreferences.AGGRESSIVE_KEY
                "stats"      -> DefensePreferences.STATS_KEY
                "night"      -> DefensePreferences.NIGHT_KEY
                "kill"       -> DefensePreferences.KILL_KEY
                else -> return@launch
            }
            defensePrefs.saveSetting(key, isEnabled)
        }
    }

    init {
        ejecutarDiagnostico()
    }

    fun ejecutarDiagnostico() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val datos = repository.obtenerTodosDatos()
            datos.score
                .onSuccess { score ->
                    _uiState.update {
                        it.copy(
                            scoreVicio = score,
                            historyData = datos.grafica,
                            topApps = datos.topApps,
                            desgloseCategorias = datos.desgloseCategorias,
                            promedioSemanal = datos.promedioSemanal,
                            diaMasVicioso = datos.diaMasVicioso,
                            tendencia = datos.tendencia,
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