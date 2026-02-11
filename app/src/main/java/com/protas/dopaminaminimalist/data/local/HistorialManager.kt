package com.protas.dopaminaminimalist.data.local


import android.content.Context
import org.json.JSONArray
import java.io.File
import java.io.IOException

class HistorialManager(private val context: Context) {

    private val fileName = "historial_vicio.json"
    private val diasRequeridos = 30
    private val featuresPorDia = 20

    /**
     * Actualiza el historial:
     * 1. Carga lo que hay guardado.
     * 2. Agrega los datos de HOY al final.
     * 3. Si hay más de 30 días, borra el más antiguo (ventana deslizante).
     * 4. Guarda de nuevo en el archivo.
     */
    fun updateHistory(statsHoy: FloatArray) {
        val historialActual = cargarDesdeDisco().toMutableList()

        // Agregamos el día de hoy
        historialActual.add(statsHoy)

        // Si nos pasamos de 30 días, eliminamos el primero (el más viejo)
        if (historialActual.size > diasRequeridos) {
            historialActual.removeAt(0)
        }

        guardarEnDisco(historialActual)
    }

    /**
     * Prepara la matriz exacta que necesita TensorFlow Lite: [1, 30, 20]
     * (1 Usuario, 30 Días, 20 Características)
     * * Si el usuario lleva menos de 30 días usando la app, rellenamos
     * el principio con ceros (Padding) para no romper el modelo.
     */
    /**
     * Prepara la matriz [1, 30, 20].
     * CORRECCIÓN: Si faltan días, clonamos el último día conocido
     * para que el promedio no se diluya con ceros.
     */
    fun getDataMatrix(): Array<Array<FloatArray>> {
        val historial = cargarDesdeDisco()

        // Si no hay historial aún, devolvemos ceros
        if (historial.isEmpty()) {
            return arrayOf(Array(diasRequeridos) { FloatArray(featuresPorDia) { 0f } })
        }

        val matriz30Dias = Array(diasRequeridos) { FloatArray(featuresPorDia) }

        // El "Día Modelo" es el último día registrado (Hoy)
        val diaModelo = historial.last()

        // Llenamos la matriz
        for (i in 0 until diasRequeridos) {
            // Calculamos qué índice del historial corresponde
            // Si el historial es corto (ej. 1 día), índiceHistorial siempre apuntará a ese día.
            val indiceHistorial = if (i >= diasRequeridos - historial.size) {
                i - (diasRequeridos - historial.size)
            } else {
                // Si estamos en los días vacíos (relleno), usamos el "Día Modelo"
                // Esto engaña a la IA para que crea que llevas 30 días así.
                -1
            }

            if (indiceHistorial >= 0) {
                matriz30Dias[i] = historial[indiceHistorial]
            } else {
                // CLONACIÓN: Rellenamos el pasado vacío con el comportamiento de hoy
                matriz30Dias[i] = diaModelo.clone()
            }
        }

        return arrayOf(matriz30Dias)
    }

    // --- MÉTODOS PRIVADOS DE ALMACENAMIENTO (JSON NATIVO) ---

    private fun guardarEnDisco(lista: List<FloatArray>) {
        try {
            val jsonArray = JSONArray()
            for (dia in lista) {
                val diaJson = JSONArray()
                for (valor in dia) {
                    diaJson.put(valor.toDouble())
                }
                jsonArray.put(diaJson)
            }

            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(jsonArray.toString().toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun cargarDesdeDisco(): List<FloatArray> {
        val lista = mutableListOf<FloatArray>()
        val file = File(context.filesDir, fileName)

        if (!file.exists()) return lista

        try {
            val jsonString = context.openFileInput(fileName).bufferedReader().use {
                it.readText()
            }

            if (jsonString.isEmpty()) return lista

            val jsonArray = JSONArray(jsonString)

            // Recorremos los días
            for (i in 0 until jsonArray.length()) {
                val diaJson = jsonArray.getJSONArray(i)
                val vectorDia = FloatArray(featuresPorDia)

                // Recorremos las 20 características de ese día
                for (j in 0 until diaJson.length()) {
                    // Protegemos por si el JSON tiene tamaños distintos
                    if (j < featuresPorDia) {
                        vectorDia[j] = diaJson.getDouble(j).toFloat()
                    }
                }
                lista.add(vectorDia)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si el archivo está corrupto, devolvemos lista vacía para no crashear
            return emptyList()
        }
        return lista
    }

    // Agrega esto dentro de la clase HistorialManager
    fun obtenerDatosGrafica(): List<Float> {
        val historial = cargarDesdeDisco()
        // Tomamos los últimos 7 días
        val ultimosDias = historial.takeLast(7)

        // Convertimos cada día (array de 20 numeros) en un solo número (la suma del tiempo)
        return ultimosDias.map { dia ->
            // Sumamos todo el tiempo de uso de ese día
            dia.sum()
        }
    }
}