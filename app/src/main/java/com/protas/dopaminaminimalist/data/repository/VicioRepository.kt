package com.protas.dopaminaminimalist.data.repository

import android.util.Log // <--- Importante
import com.protas.dopaminaminimalist.data.local.HistorialManager
import com.protas.dopaminaminimalist.data.local.UsageProvider
import com.protas.dopaminaminimalist.data.ml.VicioAnalyzer

class VicioRepository(
    private val analyzer: VicioAnalyzer,
    private val usageProvider: UsageProvider,
    private val historyManager: HistorialManager
) {

    suspend fun obtenerAnalisisCompleto(): Result<Float> {
        return try {
            // 1. Obtener datos
            val statsHoy = usageProvider.getDailyStats()

            // --- LOG 1: ¿QUÉ SALE DEL USAGE PROVIDER? ---
            Log.e("VICIO_DEBUG", "--- DATOS CRUDOS DE HOY ---")
            Log.e("VICIO_DEBUG", "Redes (Col 0): ${statsHoy[0]}")
            Log.e("VICIO_DEBUG", "Juegos/Video (Col 1): ${statsHoy[1]}")
            Log.e("VICIO_DEBUG", "Prod (Col 2): ${statsHoy[2]}")
            Log.e("VICIO_DEBUG", "Nocturno (Col 4): ${statsHoy[4]}")
            Log.e("VICIO_DEBUG", "Scroll (Col 5): ${statsHoy[5]}")
            // ---------------------------------------------

            historyManager.updateHistory(statsHoy)
            val matrizInput = historyManager.getDataMatrix()

            // --- LOG 2: ¿QUÉ ENTRA A LA IA EXACTAMENTE? ---
            // Imprimimos el último día de la matriz (el día 30)
            val dia30 = matrizInput[0][29]
            Log.e("VICIO_DEBUG", "Vector Final a la IA: ${dia30.contentToString()}")

            // 2. Ejecutar IA
            val score = analyzer.predict(matrizInput)

            // --- LOG 3: ¿QUÉ DICE LA IA? ---
            Log.e("VICIO_DEBUG", ">>> PREDICCIÓN IA: $score")

            Result.success(score)

        } catch (e: Exception) {
            Log.e("VICIO_DEBUG", "ERROR FATAL: ${e.message}")
            Result.failure(e)
        }
    }

    fun obtenerHistorialGrafica(): List<Float> {
        return historyManager.obtenerDatosGrafica()
    }
}