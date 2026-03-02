package com.protas.dopaminaminimalist.ui.PermissionManager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
// Importamos las funciones que creaste en el Paso 1
import com.protas.dopaminaminimalist.utils.*

/**
 * Controlador principal de la lógica de permisos.
 */
@Composable
fun PermissionManagerScreen(onAllPermissionsGranted: () -> Unit) {
    val context = LocalContext.current

    // Estado para rastrear qué permiso falta
    var currentStep by remember { mutableStateOf(getNextPermissionStep(context)) }

    // Re-verificar cada vez que el usuario vuelve a la app desde Ajustes
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        val next = getNextPermissionStep(context)
        if (next == PermissionStep.ALL_GRANTED) {
            onAllPermissionsGranted()
        } else {
            currentStep = next
        }
    }

    // Lanzador específico para el permiso de notificaciones (Android 13+)
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            currentStep = getNextPermissionStep(context)
            if (currentStep == PermissionStep.ALL_GRANTED) onAllPermissionsGranted()
        }
    )

    PermissionScreenContent(
        step = currentStep,
        onGrantClick = {
            when (currentStep) {
                PermissionStep.USAGE_STATS -> {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
                PermissionStep.OVERLAY -> {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                }
                PermissionStep.BATTERY -> {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}")))
                }
                PermissionStep.NOTIFICATIONS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                PermissionStep.ALL_GRANTED -> onAllPermissionsGranted()
            }
        }
    )
}

enum class PermissionStep {
    USAGE_STATS, OVERLAY, NOTIFICATIONS, BATTERY, ALL_GRANTED
}

fun getNextPermissionStep(context: Context): PermissionStep {
    return when {
        !context.hasUsageStatsPermission() -> PermissionStep.USAGE_STATS
        !context.hasOverlayPermission() -> PermissionStep.OVERLAY
        !context.hasNotificationPermission() -> PermissionStep.NOTIFICATIONS
        !context.isIgnoringBatteryOptimizations() -> PermissionStep.BATTERY
        else -> PermissionStep.ALL_GRANTED
    }
}

@Composable
fun PermissionScreenContent(step: PermissionStep, onGrantClick: () -> Unit) {
    val (title, description) = when (step) {
        PermissionStep.USAGE_STATS -> "Acceso al Uso" to "Necesito ver qué apps usas para medir tu tiempo en pantalla y ayudarte a concentrarte."
        PermissionStep.OVERLAY -> "Mostrar sobre otras apps" to "Esto permite que enfocaAPP bloquee distracciones visualmente."
        PermissionStep.NOTIFICATIONS -> "Notificaciones" to "Para enviarte recordatorios de bienestar y alertas de límite de uso."
        PermissionStep.BATTERY -> "Sin límites de batería" to "Para que el monitoreo de bienestar funcione siempre correctamente."
        else -> "¡Todo listo!" to "Ya puedes empezar a usar enfocaAPP."
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onGrantClick) {
                Text("Conceder Permiso")
            }
        }
    }
}