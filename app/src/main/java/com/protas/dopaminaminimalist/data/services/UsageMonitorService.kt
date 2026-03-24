package com.protas.dopaminaminimalist.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.protas.dopaminaminimalist.MainActivity
import com.protas.dopaminaminimalist.barrier.BarrierActivity
// import com.protas.dopaminaminimalist.presentation.BarrierActivity // Descomenta cuando tengas la BarrierActivity
import java.util.TreeMap

class UsageMonitorService : Service() {

    private val CHECK_INTERVAL = 600000L // 10 minutos (10 * 60 * 1000 ms)
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "vicio_channel_id"

    private lateinit var usageStatsManager: UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())

    // Evitar spam de notificaciones (guardamos el último minuto notificado)
    private var ultimoMinutoNotificado: Long = -1

    private val monitorRunnable = object : Runnable {
        override fun run() {
            escanearVicio()
            handler.postDelayed(this, CHECK_INTERVAL)
        }
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // 1. Crear el canal de notificaciones (Obligatorio Android 8+)
        crearCanalDeNotificacion()

        // 2. Iniciar el servicio en primer plano para que Android no lo mate
        startForeground(NOTIFICATION_ID, crearNotificacionPersistente())

        // 3. Arrancar el loop de vigilancia
        handler.post(monitorRunnable)
        Log.d("VICIO_SERVICE", "👮 Servicio de Vigilancia Iniciado")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(monitorRunnable) // Detener el loop si se destruye
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Si el sistema mata el servicio, que lo reviva automáticamente
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // --- LÓGICA DE DETECCIÓN ---

    private fun escanearVicio() {
        if (!tienePermisoUsageStats()) return

        val time = System.currentTimeMillis()
        // Consultamos apps usadas en los últimos 10 segundos
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time)

        if (stats != null && stats.isNotEmpty()) {
            val sortedMap = TreeMap<Long, android.app.usage.UsageStats>()
            for (usageStats in stats) {
                sortedMap[usageStats.lastTimeUsed] = usageStats
            }

            if (sortedMap.isNotEmpty()) {
                val currentApp = sortedMap[sortedMap.lastKey()]
                val pkgName = currentApp!!.packageName

                // Si la app actual es viciosa...
                if (esAppViciosa(pkgName)) {
                    val minutosHoy = currentApp.totalTimeInForeground / (1000 * 60)
                    Log.d("VICIO_SERVICE", "🚨 Detectado: $pkgName | Tiempo: $minutosHoy min")

                    gestionarCastigo(pkgName, minutosHoy)
                }
            }
        }
    }

    private fun gestionarCastigo(pkgName: String, minutos: Long) {
        // REGLA 1: Notificar cada 5 minutos (5, 10, 15...)
        if (minutos > 0 && minutos % 5 == 0L) {
            if (minutos != ultimoMinutoNotificado) {
                lanzarAlerta(pkgName, minutos)
                ultimoMinutoNotificado = minutos
            }
        }

        // REGLA 2: ACTIVAR BARRERA (Opcional, descomentar cuando tengas la Activity)

        if (minutos > 30) { // Si lleva más de 30 min, bloqueo duro
             val intent = Intent(this, BarrierActivity::class.java)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)
        }

    }

    private fun lanzarAlerta(pkgName: String, minutos: Long) {
        val nombreApp = obtenerNombreBonito(pkgName)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚠️ ALERTA DE DOPAMINA")
            .setContentText("Llevas $minutos minutos en $nombreApp. ¡Cierra eso!")
            .setSmallIcon(android.R.drawable.stat_sys_warning) // Cambia por tu icono: R.drawable.ic_logo
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Vibración molesta
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // --- UTILIDADES ---

    private fun esAppViciosa(pkg: String): Boolean {
        val p = pkg.lowercase()
        // Lista negra hardcodeada (luego puedes sacarla de una base de datos)
        return p.contains("tiktok") ||
                p.contains("instagram") ||
                p.contains("facebook") ||
                p.contains("youtube") ||
                p.contains("twitter") ||
                p.contains("twitch")
    }


    private fun obtenerNombreBonito(pkg: String): String {
        return when {
            pkg.contains("tiktok") -> "TikTok"
            pkg.contains("instagram") -> "Instagram"
            pkg.contains("youtube") -> "YouTube"
            pkg.contains("facebook") -> "Facebook"
            pkg.contains("twitch") -> "Twitch"
            else -> "esta app"
        }
    }

    private fun crearNotificacionPersistente(): Notification {
        // Notificación fija que dice "Te estoy vigilando" (Obligatoria para Foreground Service)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🛡️ Escudo Activo")
            .setContentText("Protegiendo tu atención en segundo plano.")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // No se puede quitar deslizando
            .build()
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vigilancia de Dopamina",
                NotificationManager.IMPORTANCE_LOW // Low para la persistente, High para las alertas
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun tienePermisoUsageStats(): Boolean {
        // Verificación rápida de permiso (aunque se supone que ya lo pediste en la UI)
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }


}