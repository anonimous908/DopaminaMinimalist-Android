package com.protas.dopaminaminimalist.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.protas.dopaminaminimalist.ui.components.VicioCard
import com.protas.dopaminaminimalist.ui.components.UsageGraph

@OptIn(ExperimentalMaterial3Api::class) // Necesario para LargeTopAppBar
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    // Observamos el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "enfocaApp",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Barra de carga
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // 1. Tarjeta del porcentaje
            VicioCard(score = uiState.scoreVicio)

            // 2. AQUÍ AGREGAMOS LA GRÁFICA (Ahora dejará de estar gris)
            UsageGraph(dataPoints = uiState.historyData)

            // Sección de recomendación
            Text(
                text = "Recomendación Personalizada",
                modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                style = MaterialTheme.typography.titleLarge
            )

            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = if (uiState.scoreVicio > 0.7f)
                        "⚠️ Tu uso es alto. Intenta dejar el celular 1 hora."
                    else "✅ ¡Buen trabajo! Mantienes un equilibrio saludable.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}