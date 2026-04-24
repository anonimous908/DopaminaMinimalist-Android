package com.protas.dopaminaminimalist.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
// Asegúrate de que esta ruta sea idéntica a donde guardaste tu componente de gráfica
import com.protas.dopaminaminimalist.ui.components.UsageGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Si LargeTopAppBar sigue rojo, intenta cambiarlo por CenterAlignedTopAppBar
            LargeTopAppBar(
                title = { Text("Estadísticas") },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            state.datos?.let { datos ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Desglose por Apps",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Tarjeta para el gráfico circular (Pie Chart)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text("Gráfico de Categorías próximamente")
                        }
                    }

                    Text(
                        text = "Tendencia de esta semana",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Reutilizamos tu componente de gráfica existente
                    UsageGraph(dataPoints = datos.grafica)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoCard(
                            titulo = "Promedio",
                            valor = "${String.format("%.1f", datos.promedioSemanal)} h",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            titulo = "Día Top",
                            valor = datos.diaMasVicioso,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.headlineMedium)
        }
    }
}