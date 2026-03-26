package com.protas.dopaminaminimalist.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.protas.dopaminaminimalist.ui.screens.armas.ArmasScreen
import com.protas.dopaminaminimalist.ui.screens.progreso.ProgresoScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
// IMPORTAMOS NUESTROS COLORES CENTRALIZADOS
import com.protas.dopaminaminimalist.ui.theme.*
@Composable
fun EnfocaApp(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // 1. RECOLECTAR EL ESTADO REAL: Ya no usamos el 'remember' local
    val settings by viewModel.settingsState.collectAsState()

    var tab by remember { mutableStateOf("home") }
    val score = (uiState.scoreVicio * 100).toInt()
    // Calculamos cuántas armas hay activas en el DataStore actualmente
    val activeCount = settings.values.count { it }
// Lógica para el color del nivel según el score
    val levelColor = when {
        score < 40 -> VicioBajo
        score < 70 -> VicioMedio
        else       -> VicioAlto
    }

    // Etiqueta visual según el nivel de vicio
    val levelLabel = when {
        score < 40 -> "Vas bien 🟢"
        score < 70 -> "Cuidado 🟡"
        else       -> "Peligro 🔴"
    }
    //Surface funciona como contenedor base de la UI.
    Surface(color = BgPage, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedContent(targetState = tab, label = "tab") { currentTab ->
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
                        "armas"    -> ArmasScreen(activeWeapons = settings, onToggle = { id, enabled -> viewModel.toggleSetting(id, enabled) })
                        "progreso" -> ProgresoScreen(
                            historyData = uiState.historyData,
                            promedioSemanal = uiState.promedioSemanal,
                            tendencia = uiState.tendencia,
                            diaMasVicioso = uiState.diaMasVicioso,
                            topApps = uiState.topApps
                        )
                    }
                }
            }

            BottomNav(
                selected = tab,//Le dice al BottomNav cuál pestaña está activa actualmente
                onSelect = { tab = it },//Cuando el usuario toca un ícono, el BottomNav llama esta función ("home", "armas", o "progreso")
                modifier = Modifier.align(Alignment.BottomCenter)//Le dice que se pegue al centro inferior de la pantalla.
            )
        }
    }
}