package com.protas.dopaminaminimalist.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
    val dataStore = OnBoardingPreferences(context)
    val scope = rememberCoroutineScope()
    //investiga cuantas lineas tiene onBoardingData
    val pagerState = rememberPagerState(pageCount = { listaPaginas.size })
    /*
    var checkedState empieza en falce si el usuario marcó
    el checkbox de políticas cambia a true nesesario para activar boton
    popUpTo
     */

    var checkedState by remember { mutableStateOf(false) }
    /*mostrarPoliticaCompleta — guarda true/else para
    controlar si el diálogo flotante de privacidad está visible o no.
     Empieza en else (oculto), se pone true cuando el usuario toca "política de privacidad",
      */
    var mostrarPoliticaCompleta by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)

        //page[] lo busca de listaPaginas (está en mismo paquete no es necesario import)
        // y lo trae para aca y lo de abajo le dice composte
        // cómo y de qué tamaño dibujar cada dato de ese objeto.
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
                            //Función para guardar que YA lo vimos en StoreBoarding
                            dataStore.saveBoarding(true)
                            // Dentro de tu OnBoardingScreen.kt, en el onClick del botón:
                            navController.navigate("main_flow") {
                                //elimina el onboarding del stack de navegación, entonces el usuario no puede regresar a él
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