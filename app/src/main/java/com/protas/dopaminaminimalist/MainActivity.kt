package com.protas.dopaminaminimalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.protas.dopaminaminimalist.data.local.HistorialManager
import com.protas.dopaminaminimalist.data.local.UsageProvider
import com.protas.dopaminaminimalist.data.ml.VicioAnalyzer
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import com.protas.dopaminaminimalist.ui.screens.home.HomeScreen
import com.protas.dopaminaminimalist.ui.screens.home.HomeViewModel
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme // Si te marca error aquí, revisa la nota abajo*
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.protas.dopaminaminimalist.presentation.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Con 'by lazy', estas herramientas NO se crean al abrir la app.
    // Solo se crean en el momento exacto en que alguien las llama.
    // 1. Inicialización de dependencias
    private val analyzer by lazy { VicioAnalyzer(this) }
    private val provider by lazy { UsageProvider(this) }
    private val history by lazy { HistorialManager(this) }

    // 2. Creamos el Repositorio
    private val repository by lazy { VicioRepository(analyzer, provider, history) }
    // 3. Creamos el ViewModel
    private val viewModel by lazy { HomeViewModel(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DopaminaMinimalistTheme {
                // 1. Creamos el controlador de navegación
                val navController = androidx.navigation.compose.rememberNavController()

                // 2. Definimos el mapa de pantallas (NavHost)
                androidx.navigation.compose.NavHost(
                    navController = navController,
                    startDestination = "onboarding_screen" // Aquí arranca la app
                ) {

                    // PANTALLA A: Las políticas de privacidad
                    composable("onboarding_screen") {
                        // Asegúrate de que importe tu OnBoardingScreen nuevo
                        com.protas.dopaminaminimalist.avisos_privacidad.OnBoardingScreen(navController)
                    }

                    // PANTALLA B: Tu flujo original (Permisos y Gráficas)
                    composable("main_flow") {
                        if (hasUsageStatsPermission()) {
                            // Si ya dio permiso en el sistema, va a la app
                            MainPagerContainer(viewModel = viewModel)
                        } else {
                            // Si no, le pide el permiso de uso de apps
                            PermissionScreen {
                                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            }
                        }
                    }
                }
            }
        }


    }

    // Función mágica para saber si tienes permiso
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

// Pantalla simple para pedir el permiso
@Composable
fun PermissionScreen(onGrantClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
            androidx.compose.material3.Text("Necesito ver tu uso de apps para juzgarte.")
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Button(onClick = onGrantClick) {
                androidx.compose.material3.Text("Conceder Permiso")
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
                    0 -> StatsPlaceholder() // Pantalla de la izquierda
                    1 -> HomeScreen(viewModel = viewModel) // Pantalla central
                    2 -> SettingsScreen() // Pantalla de la derecha

        }
    }
}

@Composable
fun StatsPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("📊 Historial Detallado (Próximamente)", style = MaterialTheme.typography.headlineMedium)
    }
}