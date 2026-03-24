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
import com.protas.dopaminaminimalist.onboarding.OnBoardingPreferences
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme
import com.protas.dopaminaminimalist.onboarding.OnBoardingScreen
// Importamos tu nuevo gestor de permisos y las extensiones
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionManagerScreen
import com.protas.dopaminaminimalist.ui.screens.permission.getNextPermissionStep
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionStep
import com.protas.dopaminaminimalist.ui.navigation.EnfocaApp
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModelFactory

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
                // SOLUCIÓN 1: Crear el NavController
                val navController = rememberNavController()

                val context = LocalContext.current
                val dataStore = OnBoardingPreferences(context)

                // SOLUCIÓN 2: Asegúrate de que el import arriba sea:
                // import androidx.compose.runtime.collectAsState
                val isOnboardingCompleted by dataStore.getBoarding.collectAsState(initial = null)

                if (isOnboardingCompleted != null) {
                    val startDestination = if (isOnboardingCompleted == true) "main_flow" else "onboarding_screen"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("onboarding_screen") {
                            OnBoardingScreen(navController)
                        }

                        composable("main_flow") {
                            val currentContext = LocalContext.current
                            var allPermsGranted by remember {
                                mutableStateOf(getNextPermissionStep(currentContext) == PermissionStep.ALL_GRANTED)
                            }

                            if (allPermsGranted) {
                                EnfocaApp(viewModel = viewModel)
                            } else {
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
}