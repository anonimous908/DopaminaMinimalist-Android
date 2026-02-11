package com.protas.dopaminaminimalist.ui.screens.home

data class HomeUiState(
    val scoreVicio: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Agregamos esta lista con datos de prueba para que la gráfica no salga vacía
    val historyData: List<Float> = emptyList(), // <--- CAMBIA ESTO A emptyList()
    val topApps: List<String> = emptyList()
)