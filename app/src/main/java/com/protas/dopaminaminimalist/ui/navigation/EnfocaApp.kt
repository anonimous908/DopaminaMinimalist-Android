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
    //esta línea detecta el cambio y ordena
    //a toda la pantalla actualizarse con los nuevos números.
    val uiState by viewModel.uiState.collectAsState()
    //Crea una variable local que recuerda qué pestaña
    // está viendo el usuario actualmente (por defecto, "home").
    var tab by remember { mutableStateOf("home") }

    val activeWeapons = remember { mutableStateMapOf<String, Boolean>() }
    // contador dinámico
    val activeCount = activeWeapons.values.count { it }

    val score = (uiState.scoreVicio * 100).toInt()


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
                        "armas"    -> ArmasScreen(activeWeapons)
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