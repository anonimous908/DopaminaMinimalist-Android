package com.protas.dopaminaminimalist.data.repository

import android.util.Log
import com.protas.dopaminaminimalist.data.datasource.AppUsageInfo
import com.protas.dopaminaminimalist.data.datasource.UsageProvider
import com.protas.dopaminaminimalist.data.ai.AddictionAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DatosCompletos(
    val score: Result<Float>,
    val grafica: List<Float>,
    val topApps: List<AppUsageInfo>,
    val desgloseCategorias: Map<String, Float>,
    val promedioSemanal: Float,
    val diaMasVicioso: String,
    val tendencia: Float
)

class VicioRepository(
    private val analyzer: AddictionAnalyzer,
    private val usageProvider: UsageProvider
) {
    suspend fun obtenerTodosDatos(): DatosCompletos {
        return withContext(Dispatchers.IO) {
            // UNA sola vez — todo reutiliza estos datos
            val ultimos30Dias = usageProvider.recuperarUltimos30Dias()
            val topApps = usageProvider.obtenerTopApps()

            // IA
            val matrizInput = arrayOf(ultimos30Dias.toTypedArray())
            val score = try {
                Result.success(analyzer.predict(matrizInput))
            } catch (e: Exception) {
                Log.e("VICIO_DEBUG", "ERROR IA: ${e.message}")
                Result.failure(e)
            }

            // Gráfica 7 días
            val grafica = ultimos30Dias.takeLast(7).map { dia ->
                dia[0] + dia[1] + dia[2] + dia[3]
            }

            // Desglose por categoría
            val desglose = topApps
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.timeInHours.toDouble() }.toFloat() }

            // Estadísticas semana
            val ultimos7Dias = ultimos30Dias.takeLast(7)
            val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            val promedio = ultimos7Dias.map { dia ->
                dia[0] + dia[1] + dia[2] + dia[19] // Social + Video + Productividad + Otros
            }.average().toFloat()
            val indiceDiaMasVicioso = ultimos7Dias.indices.maxByOrNull {
                ultimos7Dias[it].sum()
            } ?: 0
            val diaMasVicioso = dias[indiceDiaMasVicioso % 7]
            val primeraMitad = ultimos7Dias.take(3).map { dia ->
                dia[0] + dia[1] + dia[2] + dia[19]
            }.average().toFloat()

            val segundaMitad = ultimos7Dias.takeLast(3).map { dia ->
                dia[0] + dia[1] + dia[2] + dia[19]
            }.average().toFloat()
            val tendencia = primeraMitad - segundaMitad

            DatosCompletos(score, grafica, topApps, desglose, promedio, diaMasVicioso, tendencia)
        }
    }
}