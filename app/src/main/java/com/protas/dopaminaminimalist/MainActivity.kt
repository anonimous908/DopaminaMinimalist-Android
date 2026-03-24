package com.protas.dopaminaminimalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.protas.dopaminaminimalist.data.datasource.UsageProvider
import com.protas.dopaminaminimalist.data.ai.AddictionAnalyzer
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import com.protas.dopaminaminimalist.ui.screens.settings.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme
import com.protas.dopaminaminimalist.onboarding.OnBoardingScreen
// Importamos tu nuevo gestor de permisos y las extensiones
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionManagerScreen
import com.protas.dopaminaminimalist.ui.screens.permission.getNextPermissionStep
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionStep
import com.protas.dopaminaminimalist.ui.navigation.EnfocaApp
import com.protas.dopaminaminimalist.ui.screens.settings.HomeViewModelFactory

class MainActivity : ComponentActivity() {

    // Inicialización de dependencias (Lazy para optimizar recursos)

    // — prepara el motor de análisis de comportamiento de uso
    private val analyzer by lazy { AddictionAnalyzer(this) }

    // prepara el acceso a las estadísticas de uso de apps del sistema
    private val provider by lazy { UsageProvider(this) }
    private val repository by lazy { VicioRepository(analyzer, provider) }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //prepara el ViewModel con el repository listo para dárselo a la UI
        val factory = HomeViewModelFactory(repository)
        val viewModel: HomeViewModel by viewModels { factory }
        setContent {
            DopaminaMinimalistTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "onboarding_screen"//destinacion inicial
                ) {
                    /* PANTALLA A: Políticas de privacidad
                    Como OnBoardingScreen no necesita datos de uso de apps
                    solo necesita el navController para saber a dónde navegar
                    y en cambio el viewModel contiene datos de monitoreo de apps,
                    y en la pantalla de onboarding todavía no se necesita nada de eso.

                    */
                    composable("onboarding_screen") {
                        OnBoardingScreen(navController)
                    }

                    // PANTALLA B: Flujo principal con validación de permisos
                    composable("main_flow") {
                        val context = LocalContext.current

                        // Verifica en tiempo real si el usuario ya otorgó todos los permisos necesarios
                        var allPermsGranted by remember {
                            mutableStateOf(getNextPermissionStep(context) == PermissionStep.ALL_GRANTED)
                        }
                        // BIFURCACIÓN: permisos completos → app | permisos faltantes → wizard
                        if (allPermsGranted) {
                            // Si_todo está ok, entramos a la app (enfocaAPP)
                            EnfocaApp(viewModel = viewModel)
                        } else {

                            // Cuando el wizard termina, actualiza el estado y entra a la app
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