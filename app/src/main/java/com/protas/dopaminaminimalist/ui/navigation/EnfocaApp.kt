package com.protas.dopaminaminimalist.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.protas.dopaminaminimalist.ui.screens.armas.ArmasScreen
import com.protas.dopaminaminimalist.ui.screens.progreso.ProgresoScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.screens.settings.SettingsScreen // Asegúrate de que este import sea correcto
import com.protas.dopaminaminimalist.ui.theme.*

@Composable
fun EnfocaApp(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    var tab by remember { mutableStateOf("home") }

    val score = (uiState.scoreVicio * 100).toInt()
    val activeCount = settings.values.count { it }

    val levelColor = when {
        score < 40 -> VicioBajo
        score < 70 -> VicioMedio
        else       -> VicioAlto
    }

    val levelLabel = when {
        score < 40 -> "Vas bien 🟢"
        score < 70 -> "Cuidado 🟡"
        else       -> "Peligro 🔴"
    }

    Surface(color = BgPage, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
                // HE QUITADO EL SCROLL DE AQUÍ. Cada pantalla debe tener su propio scroll interno.
            ) {
                AnimatedContent(
                    targetState = tab,
                    label = "tab",
                    modifier = Modifier.fillMaxSize() // Esto fuerza a que ocupe todo el espacio
                ) { currentTab ->
                    when (currentTab) {
                        "home"     -> HomeScreen(
                            score = score,
                            levelColor = levelColor,
                            levelLabel = levelLabel,
                            activeCount = activeCount,
                            topApps = uiState.topApps,
                            isLoading = uiState.isLoading,
                            onGoToArmas = { tab = "armas" }
                        )
                        "armas"    -> ArmasScreen(
                            activeWeapons = settings,
                            onToggle = { id, enabled -> viewModel.toggleSetting(id, enabled) }
                        )
                        "progreso" -> ProgresoScreen(
                            historyData = uiState.historyData,
                            promedioSemanal = uiState.promedioSemanal,
                            tendencia = uiState.tendencia,
                            diaMasVicioso = uiState.diaMasVicioso,
                            topApps = uiState.topApps
                        )
                        "settings" -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }

            BottomNav(
                selected = tab,
                onSelect = { tab = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}