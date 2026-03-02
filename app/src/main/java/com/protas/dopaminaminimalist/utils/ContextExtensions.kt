package com.protas.dopaminaminimalist.utils

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat

// 1. Verificar Uso de Apps (Usage Stats)
fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

// 2. Verificar Superposición (Draw over other apps)
fun Context.hasOverlayPermission(): Boolean {
    return Settings.canDrawOverlays(this)
}

// 3. Verificar Notificaciones (Android 13+)
fun Context.hasNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
    return true // En versiones viejas se concede al instalar
}

// 4. Verificar Ignorar Optimización de Batería (Para que no maten tu servicio)
fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}