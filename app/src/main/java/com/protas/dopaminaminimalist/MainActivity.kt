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
import com.protas.dopaminaminimalist.onboarding.OnBoardingPreferences
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme
import com.protas.dopaminaminimalist.onboarding.OnBoardingScreen
// Importamos el gestor de permisos
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionManagerScreen
import com.protas.dopaminaminimalist.ui.screens.permission.getNextPermissionStep
import com.protas.dopaminaminimalist.ui.screens.permission.PermissionStep
import com.protas.dopaminaminimalist.ui.navigation.EnfocaApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // 1. CRITICO: Esta anotación permite que Hilt inyecte cosas aquí
class MainActivity : ComponentActivity() {

    // 2. Hilt se encarga de todo el "trabajo sucio".
    // Ya no necesitas 'lazy analyzer', 'lazy repository', etc.
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. ¡La "HomeViewModelFactory" ha muerto! Ya no necesitas crearla aquí.

        setContent {
            DopaminaMinimalistTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // Nota: OnBoardingPreferences aún se crea manual aquí,
                // pero lo principal (la lógica de vicio e IA) ya está en el almacén.
                val dataStore = OnBoardingPreferences(context)
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
                                // Pasamos el viewModel que Hilt nos entregó listo
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