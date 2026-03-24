package com.protas.dopaminaminimalist.data.ai

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class AddictionAnalyzer(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        try {
            val model = loadModelFile()
            // Configuración flexible para permitir Ops de TensorFlow
            val options = Interpreter.Options()
            interpreter = Interpreter(model, options)
            Log.d("VICIO_IA", "✅ Modelo TFLite cargado correctamente en memoria.")
        } catch (e: Exception) {
            Log.e("VICIO_IA", "❌ ERROR CARGANDO MODELO: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd("model_vicio_pro.tflite")
        return assetFileDescriptor.use {
            FileInputStream(it.fileDescriptor).use { fis ->
                fis.channel.map(FileChannel.MapMode.READ_ONLY, it.startOffset, it.declaredLength)
            }
        }
    }

    fun predict(inputData: Array<Array<FloatArray>>): Float {
        return try {
            if (interpreter == null) {
                Log.e("VICIO_IA", "⚠️ El intérprete es NULL. Devolviendo 0.0")
                return 0f
            }

            // El output debe coincidir con la última capa de tu Python (Dense 1 -> [1][1])
            val output = Array(1) { FloatArray(1) }

            Log.d("VICIO_IA", "🔮 Ejecutando inferencia...")
            interpreter!!.run(inputData, output)

            val resultado = output[0][0]
            Log.d("VICIO_IA", "⚡ Resultado Bruto IA: $resultado")

            return resultado
        } catch (e: Exception) {
            Log.e("VICIO_IA", "❌ ERROR EN PREDICCIÓN: ${e.message}")
            e.printStackTrace()
            0f // En caso de error devolvemos 0
        }
    }
}