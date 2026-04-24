package com.protas.dopaminaminimalist.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.protas.dopaminaminimalist.data.datasource.AppUsageInfo
import com.protas.dopaminaminimalist.ui.theme.*


@Composable
fun HomeScreen(
    score: Int,
    levelColor: Color,
    levelLabel: String,
    activeCount: Int,
    topApps: List<AppUsageInfo>,
    isLoading: Boolean,
    onGoToArmas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("ENFOCA APP", color = TextMuted, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Hola 👋", color = TextMain, fontSize = 22.sp,
                    fontWeight = FontWeight.Black)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFEF3C7))
                    .border(2.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("☀️", fontSize = 20.sp)
                    Text("Hoy", fontSize = 11.sp, fontWeight = FontWeight.Black,
                        color = Color(0xFFD97706))
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Card diagnóstico
        val peorApp = topApps.firstOrNull()
        // CORRECCIÓN 1: Convertimos explícitamente a Float
        val horasAyer = peorApp?.timeInHours?.toFloat() ?: 0f
        val totalAyer = topApps.sumOf { it.timeInHours.toDouble() }.toFloat()

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
            border = BorderStroke(2.dp, Color(0xFFFED7AA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("HOY", color = Color(0xFF9A3412), fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (totalAyer > 0f) "Llevas %.1fh en apps de ocio 😬".format(totalAyer)
                    else "Cargando datos...",
                    color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Text("La IA te recomienda:", color = TextSub, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                val recomendaciones = when {
                    score >= 70 -> listOf(
                        "⏱️" to "Barrera de 10s en ${peorApp?.appName ?: "apps distractoras"}",
                        "🧘" to "Activa Modo Monje ahora",
                        "🌙" to "Toque de Queda a las 10 PM"
                    )
                    score >= 40 -> listOf(
                        "⏱️" to "Barrera de 10s en ${peorApp?.appName ?: "apps distractoras"}",
                        "🌙" to "Toque de Queda a las 11 PM",
                        "🌫️" to "Modo Grises en la tarde"
                    )
                    else -> listOf(
                        "✅" to "Vas bien hoy, mantén el ritmo",
                        "📊" to "Activa el Contador Flotante para monitorear",
                        "🎯" to "Meta: menos de 4h hoy"
                    )
                }

                recomendaciones.forEach { (icon, text) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBg)
                            .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(icon, fontSize = 16.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                            color = TextMain, modifier = Modifier.weight(1f))
                    }
                }

                Button(
                    onClick = onGoToArmas,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF97316))
                ) {
                    Text("Activar estas armas →", fontWeight = FontWeight.Black,
                        fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Mini stats
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Vicio card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = levelColor.copy(alpha = 0.08f)),
                border = BorderStroke(2.dp, levelColor.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$score%", fontSize = 22.sp, fontWeight = FontWeight.Black,
                        color = levelColor)
                    Text("VICIO HOY", fontSize = 10.sp, color = TextMuted,
                        fontWeight = FontWeight.Bold)
                    Text(levelLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = levelColor)
                }
            }

            // Armas card
            val ac = activeCount
            val armColor = when {
                ac == 0  -> Color(0xFFEF4444)
                ac == 1  -> Color(0xFFF97316)
                ac <= 3  -> Color(0xFFF59E0B)
                ac <= 5  -> Color(0xFF84CC16)
                else     -> Color(0xFF22C55E)
            }
            val armBg = when {
                ac == 0  -> Color(0xFFFFCDD2)
                ac == 1  -> Color(0xFFFFF7ED)
                ac <= 3  -> Color(0xFFFFFBEB)
                ac <= 5  -> Color(0xFFF7FEE7)
                else     -> Color(0xFFECFDF5)
            }
            val armBorder = when {
                ac == 0  -> Color(0xFFEF9A9A)
                ac == 1  -> Color(0xFFFED7AA)
                ac <= 3  -> Color(0xFFFDE68A)
                ac <= 5  -> Color(0xFFD9F99D)
                else     -> Color(0xFFBBF7D0)
            }
            val armLabel = when {
                ac == 0  -> "Sin defensa"
                ac == 1  -> "Muy vulnerable"
                ac <= 3  -> "Defensa baja"
                ac <= 5  -> "Casi completo"
                else     -> "Máxima defensa"
            }
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = armBg),
                border = BorderStroke(2.dp, armBorder)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$ac", fontSize = 22.sp, fontWeight = FontWeight.Black, color = armColor)
                    Text("RETOS HOY", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                    Text(armLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = armColor)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Peores apps — datos reales
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgMuted),
            border = BorderStroke(2.dp, BorderBase),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PEORES APPS HOY", color = TextSub, fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))

                if (topApps.isEmpty()) {
                    Text("Sin datos aún", color = TextMuted, fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp))
                } else {
                    // CORRECCIÓN 2: Convertimos maxHours a Float
                    val maxHours = topApps.maxOf { it.timeInHours.toFloat() }.coerceAtLeast(0.1f)

                    topApps.take(3).forEach { app ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Text("📱", fontSize = 20.sp,
                                modifier = Modifier.width(28.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(app.appName, fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold, color = TextMain
                                    )
                                    // format funciona bien con Double o Float, así que aquí no hay problema
                                    Text("${"%.1f".format(app.timeInHours)}h",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFEF4444))
                                }
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(BorderBase)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            // CORRECCIÓN 3: Convertimos app.timeInHours a Float antes de dividir
                                            .fillMaxWidth(
                                                (app.timeInHours.toFloat() / maxHours)
                                                    .coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFEF4444))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// PREVIEW CORREGIDO
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        score = 45,
        levelColor = Color(0xFFF59E0B),
        levelLabel = "Cuidado 🟡",
        activeCount = 2,
        topApps = listOf(
            AppUsageInfo("com.instagram.android", "Instagram", 2.5f, "Social"),
            AppUsageInfo("com.zhiliaoapp.musically", "TikTok", 1.8f, "Social")
        ),
        isLoading = false,
        onGoToArmas = {}
    )
}