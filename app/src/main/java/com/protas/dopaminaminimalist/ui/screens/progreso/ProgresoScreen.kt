package com.protas.dopaminaminimalist.ui.screens.progreso

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.protas.dopaminaminimalist.data.datasource.AppUsageInfo
import com.protas.dopaminaminimalist.ui.theme.*

val DAYS = listOf("L", "M", "M", "J", "V", "S", "D")

@Composable
fun ProgresoScreen(
    historyData: List<Float>,
    promedioSemanal: Float,
    tendencia: Float,
    diaMasVicioso: String,
    topApps: List<AppUsageInfo>
) {
    val improved = tendencia > 0
    val bestDay = historyData.minOrNull() ?: 0f
    val worstDay = historyData.maxOrNull() ?: 0f
    val daysBelowGoal = historyData.count { it < 4f }
    val peorApp = topApps.firstOrNull()?.appName ?: "Sin datos"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("TU HISTORIAL", color = TextMuted, fontSize = 12.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 1.sp)


        Spacer(Modifier.height(18.dp))

        // Comparativa semanas
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (improved) Color(0xFFECFDF5) else Color(0xFFFEF2F2)),
            border = BorderStroke(2.dp,
                if (improved) Color(0xFFBBF7D0) else Color(0xFFFECACA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("PROMEDIO DIARIO DE OCIO", color = TextSub, fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderBase)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ESTA SEMANA", color = TextMuted, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold)
                            Text("%.1fh".format(promedioSemanal), fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = if (improved) Color(0xFF22C55E)
                                else Color(0xFFEF4444))
                        }
                    }
                    Text(if (improved) "⬇️" else "⬆️", fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp))
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderBase)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("TENDENCIA IA", color = TextMuted, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold)
                            Text(
                                if (improved) "Mejorando" else "Empeorando",
                                fontSize = 16.sp, fontWeight = FontWeight.Black,
                                color = TextMuted
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    if (improved) "✅ Tu tendencia va mejorando esta semana"
                    else "⚠️ Tu tendencia va empeorando esta semana",
                    fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (improved) Color(0xFF065F46) else Color(0xFF991B1B),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Gráfica 7 días
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgMuted),
            border = BorderStroke(2.dp, BorderBase),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("HORAS DE OCIO POR DÍA", color = TextSub, fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))

                if (historyData.isEmpty()) {
                    Text("Sin datos aún", color = TextMuted, fontSize = 13.sp)
                } else {
                    val maxVal = historyData.maxOrNull()?.coerceAtLeast(1f) ?: 1f
                    Row(
                        modifier = Modifier.fillMaxWidth().height(72.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        historyData.takeLast(7).forEachIndexed { i, curr ->
                            val barColor = when {
                                curr < 4f -> Color(0xFF22C55E)
                                curr < 6f -> Color(0xFFF59E0B)
                                else      -> Color(0xFFEF4444)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .fillMaxHeight(curr / maxVal)
                                            .clip(RoundedCornerShape(
                                                topStart = 4.dp, topEnd = 4.dp))
                                            .background(barColor)
                                            .align(Alignment.BottomCenter)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    DAYS.getOrElse(i) { "-" },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (i == historyData.size - 1)
                                        TextMain else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Stats rápidos
        val stats = listOf(
            listOf("🏆", "%.1fh".format(bestDay), "Mejor día",
                Color(0xFF22C55E), Color(0xFFECFDF5), Color(0xFFBBF7D0)),
            listOf("💀", "%.1fh".format(worstDay), diaMasVicioso.ifEmpty { "Peor día" },
                Color(0xFFEF4444), Color(0xFFFEF2F2), Color(0xFFFECACA)),
            listOf("🎯", "$daysBelowGoal/7", "Días bajo meta (4h)",
                Color(0xFF4F46E5), Color(0xFFEEF2FF), Color(0xFFC7D2FE)),
            listOf("📱", peorApp, "App más usada",
                Color(0xFFF59E0B), Color(0xFFFFFBEB), Color(0xFFFDE68A)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(0, 1).forEach { col ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(col * 2, col * 2 + 1).forEach { idx ->
                        if (idx < stats.size) {
                            val s = stats[idx]
                            @Suppress("UNCHECKED_CAST")
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = s[4] as Color),
                                border = BorderStroke(2.dp, s[5] as Color),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(s[0] as String, fontSize = 22.sp)
                                    Text(s[1] as String, fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = s[3] as Color,
                                        textAlign = TextAlign.Center)
                                    Text(s[2] as String, fontSize = 10.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Insight IA
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(2.dp, Color(0xFFDBEAFE)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp)) {
                Text("🤖", fontSize = 22.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Insight de la IA", fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E40AF))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (improved)
                            "Tu tendencia mejora. Mantén las armas activas para consolidar el hábito."
                        else
                            "Tu uso tiende a subir. Considera activar Modo Monje en días de semana.",
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E40AF), lineHeight = 18.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProgresoScreenPreview() {
    // Datos de prueba (mock data) para forzar que la pantalla renderice en el Preview
    val mockHistory = listOf(2.5f, 5.0f, 3.2f, 7.1f, 1.5f, 4.0f, 6.5f)

    ProgresoScreen(
        historyData = mockHistory,
        promedioSemanal = 4.2f,
        tendencia = -1.5f,
        diaMasVicioso = "Jueves",
        topApps = emptyList() // Pasamos una lista vacía para evitar errores de compilación
    )
}