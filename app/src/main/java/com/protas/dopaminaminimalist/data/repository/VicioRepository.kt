package com.protas.dopaminaminimalist.data.repository

import android.util.Log // <--- Importante
import com.protas.dopaminaminimalist.data.local.UsageProvider
import com.protas.dopaminaminimalist.data.ml.VicioAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VicioRepository(
    private val analyzer: VicioAnalyzer,
    private val usageProvider: UsageProvider
) {

    suspend fun obtenerAnalisisCompleto(): Result<Float> {
        return withContext(Dispatchers.IO) {
            try {
                val ultimos30Dias = usageProvider.recuperarUltimos30Dias()
                val matrizInput = arrayOf(ultimos30Dias.toTypedArray())

                // --- LOG 1: ¿QUÉ SALE DEL USAGE PROVIDER? ---
                Log.e("VICIO_DEBUG", "--- DATOS CRUDOS DE HOY ---")
                Log.e("VICIO_DEBUG", "Días recuperados: ${ultimos30Dias.size}")
                Log.e("VICIO_DEBUG", "Vector día 30: ${matrizInput[0][29].contentToString()}")
                // ---------------------------------------------

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
    }

    fun obtenerHistorialGrafica(): List<Float> {
        return usageProvider.obtenerDatosGrafica()
    }
}