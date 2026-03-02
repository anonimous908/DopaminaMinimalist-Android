package com.protas.dopaminaminimalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.protas.dopaminaminimalist.data.local.HistorialManager
import com.protas.dopaminaminimalist.data.local.UsageProvider
import com.protas.dopaminaminimalist.data.ml.VicioAnalyzer
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme
// Importamos tu nuevo gestor de permisos y las extensiones
import com.protas.dopaminaminimalist.ui.PermissionManager.PermissionManagerScreen
import com.protas.dopaminaminimalist.ui.PermissionManager.getNextPermissionStep
import com.protas.dopaminaminimalist.ui.PermissionManager.PermissionStep
import com.protas.dopaminaminimalist.ui.SettingsScreen.SettingsScreen
import com.protas.dopaminaminimalist.ui.grafica_datos_recolectados.datos_procesados

class MainActivity : ComponentActivity() {

    // Inicialización de dependencias (Lazy para optimizar recursos)
    private val analyzer by lazy { VicioAnalyzer(this) }
    private val provider by lazy { UsageProvider(this) }
    private val history by lazy { HistorialManager(this) }
    private val repository by lazy { VicioRepository(analyzer, provider, history) }
    private val viewModel by lazy { HomeViewModel(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DopaminaMinimalistTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "onboarding_screen"
                ) {
                    // PANTALLA A: Políticas de privacidad
                    composable("onboarding_screen") {
                        com.protas.dopaminaminimalist.avisos_privacidad.OnBoardingScreen(navController)
                    }

                    // PANTALLA B: Flujo principal con validación de permisos
                    composable("main_flow") {
                        val context = LocalContext.current

                        // Estado para saber si ya tenemos luz verde total
                        var allPermsGranted by remember {
                            mutableStateOf(getNextPermissionStep(context) == PermissionStep.ALL_GRANTED)
                        }

                        if (allPermsGranted) {
                            // Si todo está ok, entramos a la app (enfocaAPP)
                            MainPagerContainer(viewModel = viewModel)
                        } else {
                            // Si falta algo, lanzamos el Wizard que creaste en el paso 2
                            PermissionManagerScreen(
                                onAllPermissionsGranted = {
                                    allPermsGranted = true
                                }
                            )
                        }
                    }
                }
            }
        }



    }
}
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


