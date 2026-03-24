package com.protas.dopaminaminimalist.ui.screens.home

import com.protas.dopaminaminimalist.data.datasource.AppUsageInfo

data class HomeUiState(
    val scoreVicio: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null,
    val historyData: List<Float> = emptyList(),
    // Top 5 apps del día con nombre y tiempo
    val topApps: List<AppUsageInfo> = emptyList(),
    // Desglose por categoría: Social, Video, Productividad, Otros
    val desgloseCategorias: Map<String, Float> = emptyMap(),
    // Estadísticas de la semana
    val promedioSemanal: Float = 0f,
    val diaMasVicioso: String = "",
    val tendencia: Float = 0f // positivo = mejorando, negativo = empeorando
)