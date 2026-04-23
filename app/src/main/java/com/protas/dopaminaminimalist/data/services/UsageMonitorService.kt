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
import com.protas.dopaminaminimalist.data.dataStore.DefensePreferences
import com.protas.dopaminaminimalist.data.datasource.UsageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.TreeMap
import javax.inject.Inject
import android.provider.Settings

@AndroidEntryPoint // Permite que Hilt inyecte dependencias en este servicio
class UsageMonitorService : Service() {

    // --- Configuración del Servicio ---
    private val CHECK_INTERVAL = 600000L // 10 minutos
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "vicio_channel_id"

    // --- Dependencias Inyectadas (Hilt las entrega listas) ---
    @Inject
    lateinit var usageProvider: UsageProvider

    @Inject
    lateinit var defensePreferences: DefensePreferences

    // --- Propiedades de Control ---
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentSettings: Map<String, Boolean> = emptyMap()
    private lateinit var usageStatsManager: UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())
    private var ultimoMinutoNotificado: Long = -1

    // --- Bucle de Vigilancia ---
    private val monitorRunnable = object : Runnable {
        override fun run() {
            escanearVicio()
            handler.postDelayed(this, CHECK_INTERVAL)
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializamos el gestor del sistema
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // IMPORTANTE: Ya no inicializamos defensePreferences manualmente.
        // Hilt ya lo hizo antes de entrar a onCreate.

        crearCanalDeNotificacion()
        startForeground(NOTIFICATION_ID, crearNotificacionPersistente())

        handler.post(monitorRunnable)
        Log.d("VICIO_SERVICE", "👮 Servicio de Vigilancia Iniciado con Hilt")

        // Escuchamos cambios en los ajustes de forma reactiva
        serviceScope.launch {
            defensePreferences.getSettings.collect { settings ->
                currentSettings = settings
                Log.d("VICIO_SERVICE", "⚙️ Ajustes actualizados: $currentSettings")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // El servicio se reinicia si el sistema lo mata
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(monitorRunnable)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // --- Lógica de Detección y Castigo ---

    private fun escanearVicio() {
        if (!tienePermisoUsageStats()) return

        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time - CHECK_INTERVAL,
            time
        )

        if (stats != null && stats.isNotEmpty()) {
            val sortedMap = TreeMap<Long, android.app.usage.UsageStats>()
            for (usageStats in stats) {
                sortedMap[usageStats.lastTimeUsed] = usageStats
            }

            if (sortedMap.isNotEmpty()) {
                val currentApp = sortedMap[sortedMap.lastKey()]
                val pkgName = currentApp!!.packageName

                if (esAppViciosa(pkgName)) {
                    val minutosHoy = currentApp.totalTimeInForeground / (1000 * 60)
                    Log.d("VICIO_SERVICE", "🚨 Detectado: $pkgName | Tiempo: $minutosHoy min")
                    gestionarCastigo(pkgName, minutosHoy)
                }
            }
        }

        // Recordatorio motivacional (Grayscale)
        if (currentSettings["grayscale"] == true) {
            val minutosTotal = System.currentTimeMillis() / (1000 * 60)
            if (minutosTotal % 30 == 0L) {
                enviarRecordatorioMotivacional()
            }
        }
    }





    private fun gestionarCastigo(pkgName: String, minutos: Long) {
        // Alerta cada 5 minutos
        if (minutos > 0 && minutos % 5 == 0L) {
            if (minutos != ultimoMinutoNotificado) {
                lanzarAlerta(pkgName, minutos)
                ultimoMinutoNotificado = minutos
            }
        }

        // Bloqueo duro (Barrera)
        if (minutos > 30 && currentSettings["barrier"] == true) {
            // Verificamos si es Android 10+ y si NO tenemos el permiso de superposición
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
                // El sistema no nos deja abrir la pantalla, mandamos alerta extrema
                lanzarAlertaExtrema(pkgName, minutos)
            } else {
                // Tenemos permiso (o es un Android viejo), lanzamos la barrera
                val intent = Intent(this, BarrierActivity::class.java).apply {
                    // FLAG_ACTIVITY_CLEAR_TOP asegura que no abramos múltiples barreras infinitas
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            }
        }
    }

    // Agrega esta nueva función justo debajo
    private fun lanzarAlertaExtrema(pkgName: String, minutos: Long) {
        val nombreApp = obtenerNombreBonito(pkgName)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🛑 BLOQUEO DE EMERGENCIA")
            .setContentText("Llevas $minutos min en $nombreApp. Abre la app para activar los permisos de bloqueo.")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Hace que salga hasta arriba
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000)) // Vibración muy molesta
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun lanzarAlerta(pkgName: String, minutos: Long) {
        val nombreApp = obtenerNombreBonito(pkgName)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚠️ ALERTA DE DOPAMINA")
            .setContentText("Llevas $minutos minutos en $nombreApp. ¡Cierra eso!")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun enviarRecordatorioMotivacional() {
        val mensajes = listOf(
            "¿Sigues ahí? Tu cerebro merece un descanso. 🧠",
            "30 minutos más. ¿Realmente vale la pena?",
            "Sal un momento. El scroll puede esperar. 🌿"
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⏸️ Momento de pausa")
            .setContentText(mensajes.random())
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // --- Utilidades ---

    private fun esAppViciosa(pkg: String): Boolean {
        val p = pkg.lowercase()
        return p.contains("tiktok") || p.contains("instagram") ||
                p.contains("facebook") || p.contains("youtube") ||
                p.contains("twitter") || p.contains("twitch")
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
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🛡️ Escudo Activo")
            .setContentText("Protegiendo tu atención en segundo plano.")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vigilancia de Dopamina",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun tienePermisoUsageStats(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }
}