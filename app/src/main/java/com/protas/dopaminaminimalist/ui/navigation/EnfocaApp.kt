package com.protas.dopaminaminimalist.ui.navigation


import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.protas.dopaminaminimalist.ui.screens.armas.ArmasScreen
import com.protas.dopaminaminimalist.ui.screens.progreso.ProgresoScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel

val BgPage     = Color(0xFFF0F4FF)
val CardBg     = Color(0xFFFFFFFF)
val TextMain   = Color(0xFF111827)
val TextSub    = Color(0xFF6B7280)
val TextMuted  = Color(0xFF9CA3AF)
val BorderBase = Color(0xFFE5E7EB)
val BgMuted    = Color(0xFFF9FAFB)

@Composable
fun EnfocaApp(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var tab by remember { mutableStateOf("home") }
    val activeWeapons = remember { mutableStateMapOf<String, Boolean>() }
    val activeCount = activeWeapons.values.count { it }

    val score = (uiState.scoreVicio * 100).toInt()
    val levelColor = when {
        score < 40 -> Color(0xFF22C55E)
        score < 70 -> Color(0xFFF59E0B)
        else       -> Color(0xFFEF4444)
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
                selected = tab,
                onSelect = { tab = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}