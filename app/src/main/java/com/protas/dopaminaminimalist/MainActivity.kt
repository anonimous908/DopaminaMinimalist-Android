package com.protas.dopaminaminimalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.protas.dopaminaminimalist.data.local.HistorialManager
import com.protas.dopaminaminimalist.data.local.UsageProvider
import com.protas.dopaminaminimalist.data.ml.VicioAnalyzer
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import com.protas.dopaminaminimalist.ui.MainPagerContainer.MainPagerContainer
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme
// Importamos tu nuevo gestor de permisos y las extensiones
import com.protas.dopaminaminimalist.ui.PermissionManager.PermissionManagerScreen
import com.protas.dopaminaminimalist.ui.PermissionManager.getNextPermissionStep
import com.protas.dopaminaminimalist.ui.PermissionManager.PermissionStep

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

                        // Verifica en tiempo real si el usuario ya otorgó todos los permisos necesarios
                        var allPermsGranted by remember {
                            mutableStateOf(getNextPermissionStep(context) == PermissionStep.ALL_GRANTED)
                        }
                        // BIFURCACIÓN: permisos completos → app | permisos faltantes → wizard
                        if (allPermsGranted) {
                            // Si_todo está ok, entramos a la app (enfocaAPP)
                            MainPagerContainer(viewModel = viewModel)
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