package com.protas.dopaminaminimalist.data.datasource

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val timeInHours: Float,
    val category: String
)
class UsageProvider(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private val catGame = 0
    private val catAudio = 1
    private val catVideo = 2
    private val catSocial = 4
    private val catProductivity = 7
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



    fun obtenerDatosGrafica(): List<Float> {
        val ultimos30Dias = recuperarUltimos30Dias()
        val ultimos7Dias = ultimos30Dias.takeLast(7)
        return ultimos7Dias.map { dia -> dia.sum() }
    }



    // --- PROCESADOR CENTRAL ---
    private fun consultarYProcesar(start: Long, end: Long): FloatArray {
        val statsVector = FloatArray(20)
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, start, end
        )

        usageStats.forEach { stats ->
            val timeInHours = stats.totalTimeInForeground / (1000 * 60 * 60).toFloat()
            if (timeInHours > 0.01f && !esAppDelSistema(stats.packageName)) {
                var category = getAppCategory(stats.packageName)
                if (category == -1) {
                    category = adivinarCategoriaPorNombre(stats.packageName)
                }
                when (category) {
                    catSocial -> statsVector[0] += timeInHours
                    catGame, catVideo, catAudio -> statsVector[1] += timeInHours
                    catProductivity -> statsVector[2] += timeInHours
                    else -> statsVector[19] += timeInHours
                }
            }
        }
        // Columnas de tiempo cíclico (necesarias para el modelo TFLite)
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toFloat()
        val dia = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toFloat()

        statsVector[16] = sin(2 * PI * hora / 24).toFloat()
        statsVector[17] = cos(2 * PI * hora / 24).toFloat()
        statsVector[18] = sin(2 * PI * dia / 7).toFloat()
        statsVector[19] = cos(2 * PI * dia / 7).toFloat()

        statsVector[5] = statsVector[0] + (statsVector[1] * 0.5f)
        statsVector[4] = (statsVector[0] + statsVector[1]) * 0.2f

        return statsVector
    }
    // --- DICCIONARIO ACTUALIZADO CON TUS APPS ---
    private fun adivinarCategoriaPorNombre(pkg: String): Int {
        val name = pkg.lowercase()
        return when {
            // REDES catSocial
            name.contains("whatsapp") -> catSocial
            name.contains("facebook") -> catSocial
            name.contains("instagram") -> catSocial
            name.contains("tiktok") -> catSocial
            name.contains("twitter") -> catSocial
            name.contains("telegram") -> catSocial
            name.contains("reddit") -> catSocial
            name.contains("discord") -> catSocial
            name.contains("browser") -> catSocial // Brave, Chrome a veces se usan para vicio
            name.contains("chrome") -> catSocial

            // VIDEO / JUEGOS
            name.contains("youtube") -> catVideo
            name.contains("netflix") -> catVideo
            name.contains("steam") -> catGame    // <--- AGREGADO
            name.contains("tycoon") -> catGame   // <--- AGREGADO (Prison Empire)
            name.contains("games") -> catGame    // <--- AGREGADO (General)

            // PRODUCTIVIDAD
            name.contains("chatgpt") -> catProductivity // <--- AGREGADO
            name.contains("classroom") -> catProductivity // <--- AGREGADO
            name.contains("investing") -> catProductivity // <--- AGREGADO
            name.contains("calculator") -> catProductivity

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
    fun obtenerTopApps(): List<AppUsageInfo> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        return usageStats
            .filter { stats ->
                val timeInHours = stats.totalTimeInForeground / (1000 * 60 * 60).toFloat()
                timeInHours > 0.01f && !esAppDelSistema(stats.packageName)
            }
            .map { stats ->
                val timeInHours = stats.totalTimeInForeground / (1000 * 60 * 60).toFloat()
                val appName = try {
                    context.packageManager.getApplicationLabel(
                        context.packageManager.getApplicationInfo(stats.packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    stats.packageName
                }
                val category = when (adivinarCategoriaPorNombre(stats.packageName)) {
                    catSocial -> "Social"
                    catVideo -> "Video"
                    catGame -> "Juegos"
                    catProductivity -> "Productividad"
                    else -> "Otros"
                }
                AppUsageInfo(stats.packageName, appName, timeInHours, category)
            }
            .sortedByDescending { it.timeInHours }
            .take(5) // Top 5 apps
    }


}