package com.protas.dopaminaminimalist.ui.components

// --- IMPORTACIONES NECESARIAS ---
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VicioCard(score: Float) {
    // Animamos el progreso para que suba suavemente al cargar
    val animatedProgress by animateFloatAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "ScoreAnimation"
    )

    val colorByScore = when {
        score < 0.3f -> Color(0xFF4CAF50) // Verde: Saludable
        score < 0.7f -> Color(0xFFFFC107) // Amarillo: Advertencia
        else -> Color(0xFFF44336)         // Rojo: Riesgo Crítico
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Diagnóstico de IA",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(contentAlignment = Alignment.Center) {
                // Círculo de fondo (gris tenue)
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(160.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                // Círculo de progreso real
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(160.dp),
                    color = colorByScore,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "de Vicio",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}