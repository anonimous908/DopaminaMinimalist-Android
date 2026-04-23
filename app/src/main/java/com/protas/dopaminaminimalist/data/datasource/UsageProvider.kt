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

    // --- CONSTANTES DEL MODELO DE IA ---
    // Esto hace que el código sea profesional y fácil de mantener
    companion object {
        const val IDX_SOCIAL = 0
        const val IDX_VIDEO_JUEGOS_AUDIO = 1
        const val IDX_PRODUCTIVIDAD = 2
        const val IDX_OTROS = 3

        // Variables de tiempo cíclico
        const val IDX_HORA_SIN = 16
        const val IDX_HORA_COS = 17
        const val IDX_DIA_SIN = 18
        const val IDX_DIA_COS = 19
    }

    // --- MÁQUINA DEL TIEMPO (30 DÍAS) ---
    // --- MÁQUINA DEL TIEMPO (30 DÍAS) OPTIMIZADA ---
    fun recuperarUltimos30Dias(): List<FloatArray> {
        val calendar = Calendar.getInstance()

        // 1. Definimos el final (hoy a las 23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTotal = calendar.timeInMillis

        // 2. Definimos el inicio (hace 30 días a las 00:00:00)
        calendar.add(Calendar.DAY_OF_YEAR, -29) // 29 días hacia atrás + hoy = 30 días
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTotal = calendar.timeInMillis

        // 3. ¡UNA SOLA LLAMADA AL SISTEMA! (El gran ahorro de batería y CPU)
        val todasLasStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTotal, endTotal
        )

        // 4. Preparamos 30 "cubetas" (arrays) vacías, una para cada día
        val historial = Array(30) { FloatArray(20) }

        // 5. Variables de tiempo cíclico (se calculan una sola vez)
        val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toFloat()
        val diaActual = Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toFloat()
        val horaSin = sin(2 * PI * horaActual / 24).toFloat()
        val horaCos = cos(2 * PI * horaActual / 24).toFloat()
        val diaSin = sin(2 * PI * diaActual / 7).toFloat()
        val diaCos = cos(2 * PI * diaActual / 7).toFloat()

        // 6. Acomodamos cada estadística que nos dio Android en su día correspondiente
        todasLasStats.forEach { stats ->
            // Calculamos a qué día pertenece basándonos en el timestamp
            val diasDesdeInicio = ((stats.firstTimeStamp - startTotal) / (1000 * 60 * 60 * 24)).toInt()

            // Nos aseguramos de que caiga dentro de nuestros 30 días
            if (diasDesdeInicio in 0..29) {
                val arrayIndex = diasDesdeInicio
                val timeInHours = stats.totalTimeInForeground / (1000 * 60 * 60).toFloat()

                if (timeInHours > 0.01f && !esAppDelSistema(stats.packageName)) {
                    var category = getAppCategory(stats.packageName)
                    if (category == -1) {
                        category = adivinarCategoriaPorNombre(stats.packageName)
                    }
                    when (category) {
                        catSocial -> historial[arrayIndex][IDX_SOCIAL] += timeInHours
                        catGame, catVideo, catAudio -> historial[arrayIndex][IDX_VIDEO_JUEGOS_AUDIO] += timeInHours
                        catProductivity -> historial[arrayIndex][IDX_PRODUCTIVIDAD] += timeInHours
                        else -> historial[arrayIndex][IDX_OTROS] += timeInHours
                    }
                }
            }
        }

        // 7. Aplicamos los topes (coerceIn) y variables derivadas a cada día
        for (i in 0 until 30) {
            historial[i][IDX_SOCIAL] = historial[i][IDX_SOCIAL].coerceIn(0f, 24f)
            historial[i][IDX_VIDEO_JUEGOS_AUDIO] = historial[i][IDX_VIDEO_JUEGOS_AUDIO].coerceIn(0f, 24f)
            historial[i][IDX_PRODUCTIVIDAD] = historial[i][IDX_PRODUCTIVIDAD].coerceIn(0f, 24f)
            historial[i][IDX_OTROS] = historial[i][IDX_OTROS].coerceIn(0f, 24f)

            // Asignamos las variables de tiempo cíclico
            historial[i][IDX_HORA_SIN] = horaSin
            historial[i][IDX_HORA_COS] = horaCos
            historial[i][IDX_DIA_SIN] = diaSin
            historial[i][IDX_DIA_COS] = diaCos

            // Variables que necesita tu modelo TFLite
            historial[i][5] = historial[i][IDX_SOCIAL] + (historial[i][IDX_VIDEO_JUEGOS_AUDIO] * 0.5f)
            historial[i][4] = (historial[i][IDX_SOCIAL] + historial[i][IDX_VIDEO_JUEGOS_AUDIO]) * 0.2f
        }

        return historial.toList()
    }

    fun obtenerDatosGrafica(): List<Float> {
        val ultimos30Dias = recuperarUltimos30Dias()
        val ultimos7Dias = ultimos30Dias.takeLast(7)
        return ultimos7Dias.map { dia -> dia.sum() }
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
            name.contains("browser") -> catSocial
            name.contains("chrome") -> catSocial

            // VIDEO / JUEGOS
            name.contains("youtube") -> catVideo
            name.contains("netflix") -> catVideo
            name.contains("steam") -> catGame
            name.contains("tycoon") -> catGame
            name.contains("games") -> catGame

            // PRODUCTIVIDAD
            name.contains("chatgpt") -> catProductivity
            name.contains("classroom") -> catProductivity
            name.contains("investing") -> catProductivity
            name.contains("calculator") -> catProductivity

            else -> -1
        }
    }

    private fun esAppDelSistema(pkg: String): Boolean {
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
            .take(5)
    }
}