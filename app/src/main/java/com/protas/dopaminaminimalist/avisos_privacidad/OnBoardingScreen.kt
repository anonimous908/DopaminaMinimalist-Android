package com.protas.dopaminaminimalist.avisos_privacidad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(navController: NavController) {
    val context = LocalContext.current
    val dataStore = StoreBoarding(context)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { listaPaginas.size })

    // 1. ESTADOS NUEVOS PARA LA PRIVACIDAD
    var checkedState by remember { mutableStateOf(false) }
    var mostrarPoliticaCompleta by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val item = listaPaginas[page]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(item.colorFondo),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = item.imagen),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).padding(bottom = 32.dp)
                )
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = item.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Indicador de Puntos (Siempre visible o puedes ocultarlo en la última)
        if (pagerState.currentPage != listaPaginas.size - 1) {
            PageIndicator(pageSize = listaPaginas.size, currentPage = pagerState.currentPage)
        }

        // 2. LÓGICA DE LA ÚLTIMA PÁGINA (Checkbox + Botón)
        if (pagerState.currentPage == listaPaginas.size - 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FILA DEL CHECKBOX
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = checkedState,
                        onCheckedChange = { checkedState = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2196F3))
                    )
                    Text(text = "Acepto la ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "política de privacidad",
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable { mostrarPoliticaCompleta = true }
                    )
                }

                // BOTÓN CON VALIDACIÓN
                Button(
                    onClick = {
                        scope.launch {
                            dataStore.saveBoarding(true)
                            // Dentro de tu OnBoardingScreen.kt, en el onClick del botón:
                            navController.navigate("main_flow") {
                                popUpTo("onboarding_screen") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = checkedState // <--- ¡AQUÍ ESTÁ LA MAGIA!
                ) {
                    Text("Comenzar")
                }
            }
        }
    }

    // 3. DIÁLOGO FLOTANTE DE PRIVACIDAD
    if (mostrarPoliticaCompleta) {
        PoliticaPrivacidadDialog(onDismiss = { mostrarPoliticaCompleta = false })
    }
}

// --- TUS FUNCIONES AUXILIARES ---

@Composable
fun PageIndicator(pageSize: Int, currentPage: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageSize) { iteration ->
            val color = if (currentPage == iteration) Color(0xFF3F51B5) else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(if (currentPage == iteration) 12.dp else 8.dp)
            )
        }
    }
}