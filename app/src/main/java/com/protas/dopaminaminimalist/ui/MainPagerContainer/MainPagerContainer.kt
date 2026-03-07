package com.protas.dopaminaminimalist.ui.MainPagerContainer

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import com.protas.dopaminaminimalist.ui.SettingsScreen.SettingsScreen
import com.protas.dopaminaminimalist.ui.grafica_datos_recolectados.datos_procesados
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel

@Composable
fun MainPagerContainer(viewModel: HomeViewModel) {
    // Definimos 3 páginas: 0 (Stats), 1 (Home), 2 (Settings)
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    // El Pager permite el deslizamiento horizontal
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1 // <--- ESTO: Mantiene las páginas de los lados listas en memoria
    ) { page ->
        when (page) {
            0 -> datos_procesados(viewModel = viewModel) // Pantalla de la izquierda
            1 -> HomeScreen(viewModel = viewModel) // Pantalla central
            2 -> SettingsScreen(viewModel = viewModel) // Pantalla de la derecha

        }
    }
}