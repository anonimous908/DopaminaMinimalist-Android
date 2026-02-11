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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicialización manual de dependencias
        val analyzer = VicioAnalyzer(context = this)
        val provider = UsageProvider(context = this)
        val history = HistorialManager(context = this)

        // 2. Creamos el Repositorio
        val repository = VicioRepository(analyzer, provider, history)

        // 3. Creamos el ViewModel
        val viewModel = HomeViewModel(repository)

        setContent {
            DopaminaMinimalistTheme {
                // VERIFICACIÓN DE PERMISO EN TIEMPO REAL
                if (hasUsageStatsPermission()) {
                    // Si tiene permiso, mostramos la app normal
                    HomeScreen(viewModel = viewModel)
                } else {
                    // Si NO tiene permiso, mostramos pantalla de solicitud
                    PermissionScreen {
                        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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