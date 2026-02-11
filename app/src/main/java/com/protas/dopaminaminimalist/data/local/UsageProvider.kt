package com.protas.dopaminaminimalist.data.local

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import java.util.Calendar

class UsageProvider(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    // Constantes
    private val CAT_GAME = 0
    private val CAT_AUDIO = 1
    private val CAT_VIDEO = 2
    private val CAT_SOCIAL = 4
    private val CAT_PRODUCTIVITY = 7
    private val CAT_MAPS = 6

    // --- MÁQUINA DEL TIEMPO (30 DÍAS) ---
    fun recuperarUltimos30Dias(): List<FloatArray> {
        val listaHistorial = mutableListOf<FloatArray>()
        val calendar = Calendar.getInstance()

        for (i in 0 until 30) {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val end = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val start = calendar.timeInMillis

            listaHistorial.add(0, consultarYProcesar(start, end))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return listaHistorial
    }

    fun getDailyStats(): FloatArray {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis

        return consultarYProcesar(startTime, endTime)
    }

    // --- PROCESADOR CENTRAL ---
    private fun consultarYProcesar(start: Long, end: Long): FloatArray {
        val statsVector = FloatArray(20) { 0f }
        val usageStats = usageStatsManager.queryAndAggregateUsageStats(start, end)

        // Log.d("VICIO_ANALISIS", "--- ESCANEANDO APPS ---") // Comentado para no saturar, descomentar si necesitas

        usageStats.forEach { (packageName, stats) ->
            val timeInHours = stats.totalTimeInForeground / (1000 * 60 * 60).toFloat()

            // Filtramos apps irrelevantes (< 1 min) y del sistema
            if (timeInHours > 0.01f && !esAppDelSistema(packageName)) {

                var category = getAppCategory(packageName)
                if (category == -1 || category == ApplicationInfo.CATEGORY_UNDEFINED) {
                    category = adivinarCategoriaPorNombre(packageName)
                }

                when (category) {
                    CAT_SOCIAL -> statsVector[0] += timeInHours
                    CAT_GAME, CAT_VIDEO, CAT_AUDIO -> statsVector[1] += timeInHours
                    CAT_PRODUCTIVITY -> statsVector[2] += timeInHours
                    else -> statsVector[19] += timeInHours
                }
            }
        }

        // =====================================================================
        // 🚨 PARTE CRÍTICA RESTAURADA (SIN ESTO LA IA DA 0%) 🚨
        // =====================================================================

        // 1. INFERENCIA DE SCROLL (Col 5)
        // Si usaste WhatsApp (Col 0), también cuenta como Scroll.
        // Sumamos Col 0 + un poco de Col 1 (YouTube Shorts/TikTok)
        statsVector[5] = statsVector[0] + (statsVector[1] * 0.5f)

        // 2. INFERENCIA NOCTURNA (Col 4)
        // Estimamos que el 20% de tu vicio ocurre de noche
        statsVector[4] = (statsVector[0] + statsVector[1]) * 0.2f

        // =====================================================================

        Log.d("VICIO_DEBUG", "Final: Social=${statsVector[0]}, Video=${statsVector[1]}, SCROLL=${statsVector[5]}")
        return statsVector
    }

    // --- DICCIONARIO ACTUALIZADO CON TUS APPS ---
    private fun adivinarCategoriaPorNombre(pkg: String): Int {
        val name = pkg.lowercase()
        return when {
            // REDES
            name.contains("whatsapp") -> CAT_SOCIAL
            name.contains("facebook") -> CAT_SOCIAL
            name.contains("instagram") -> CAT_SOCIAL
            name.contains("tiktok") -> CAT_SOCIAL
            name.contains("twitter") -> CAT_SOCIAL
            name.contains("telegram") -> CAT_SOCIAL
            name.contains("reddit") -> CAT_SOCIAL
            name.contains("discord") -> CAT_SOCIAL
            name.contains("browser") -> CAT_SOCIAL // Brave, Chrome a veces se usan para vicio
            name.contains("chrome") -> CAT_SOCIAL

            // VIDEO / JUEGOS
            name.contains("youtube") -> CAT_VIDEO
            name.contains("netflix") -> CAT_VIDEO
            name.contains("steam") -> CAT_GAME    // <--- AGREGADO
            name.contains("tycoon") -> CAT_GAME   // <--- AGREGADO (Prison Empire)
            name.contains("games") -> CAT_GAME    // <--- AGREGADO (General)

            // PRODUCTIVIDAD
            name.contains("chatgpt") -> CAT_PRODUCTIVITY // <--- AGREGADO
            name.contains("classroom") -> CAT_PRODUCTIVITY // <--- AGREGADO
            name.contains("investing") -> CAT_PRODUCTIVITY // <--- AGREGADO
            name.contains("calculator") -> CAT_PRODUCTIVITY

            else -> -1
        }
    }

    private fun esAppDelSistema(pkg: String): Boolean {
        // Filtramos cosas técnicas que no son vicio
        return pkg.contains("com.android.systemui") ||
                pkg.contains("launcher") ||
                pkg.contains("googlequicksearchbox") ||
                pkg.contains("provider")
    }

    private fun getAppCategory(packageName: String): Int {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            appInfo.category
        } catch (e: Exception) {
            -1
        }
    }
}