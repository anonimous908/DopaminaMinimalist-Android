package com.protas.dopaminaminimalist.ui.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UsageGraph(dataPoints: List<Float>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Historial (Últimos 7 días)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 16.dp)
        ) {
            if (dataPoints.isEmpty()) return@Canvas

            val barWidth = size.width / (dataPoints.size * 2f)
            val maxData = dataPoints.maxOrNull() ?: 1f
            val scale = size.height / maxData

            dataPoints.forEachIndexed { index, value ->
                val barHeight = value * scale
                val xOffset = (index * 2 * barWidth) + (barWidth / 2)

                // Dibujar la barra
                drawRect(
                    color = if (value > 5f) Color(0xFFEF5350) else Color(0xFF66BB6A), // Rojo si > 5h, Verde si no
                    topLeft = Offset(xOffset, size.height - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }
        }
    }
}