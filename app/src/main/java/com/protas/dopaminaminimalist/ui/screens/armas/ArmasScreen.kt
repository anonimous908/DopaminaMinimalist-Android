package com.protas.dopaminaminimalist.ui.screens.armas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.protas.dopaminaminimalist.ui.theme.*

data class Weapon(
    val id: String,
    val icon: String,
    val name: String,
    val desc: String,
    val color: Color
)

val WEAPONS = listOf(
    Weapon("barrier", "⏱️", "Barrera de 10s",
        "Espera antes de entrar a una app", ColorBarrera),
    Weapon("grayscale",  "🌫️", "Modo Grises",
        "Pantalla sin color = menos tentación", TextSub),
    Weapon("monk",   "🧘", "Modo Monje",
        "Bloqueo total de apps distractoras", ColorMonje),
    Weapon("night",   "🌙", "Toque de Queda",
        "Todo se bloquea después de las 11 PM", ColorToque),
    Weapon("stats",     "📊", "Contador Flotante",
        "Ves cuánto llevas en pantalla en tiempo real",ColorHUD),
    // Asegúrate de que el ID de la IA coincida con una llave de DefensePreferences (ej. "notify")
    Weapon("notify",      "🤖", "IA Directa",
        "Te avisa cuando vas mal antes de que empeore", ColorIA),
)

@Composable
fun ArmasScreen(activeWeapons: Map<String, Boolean>,onToggle: (String, Boolean) -> Unit) {
    val activeCount = activeWeapons.values.count { it }

    // AQUÍ ESTÁ LA CORRECCIÓN: Se agregó fillMaxSize y verticalScroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("Tus retos activos", color = TextMain, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text("Activa las que quieras. Puedes cambiarlas cuando quieras.",
            color = TextSub, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(16.dp))

        val pillBg     = if (activeCount > 0) PillBgActive else PillBgInactive
        val pillBorder = if (activeCount > 0) PillBorderActive else PillBorderInactive
        val pillText   = if (activeCount > 0) PillTextActive  else PillTextInactive
        val pillLabel  = if (activeCount > 0)
            "✅ $activeCount arma${if (activeCount > 1) "s" else ""} encendida${if (activeCount > 1) "s" else ""}"
        else "⚠️ Ninguna arma activa"

        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(pillBg)
                .border(2.dp, pillBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(pillLabel, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                color = pillText)
            Text("${6 - activeCount} disponibles", fontSize = 11.sp,
                fontWeight = FontWeight.Bold, color = TextSub
            )
        }

        Spacer(Modifier.height(16.dp))

        WEAPONS.forEach { weapon ->
            // Corrección: Evaluamos si el estado es 'true' (o false por defecto si es null)
            val on = activeWeapons[weapon.id] == true

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (on) weapon.color.copy(alpha = 0.07f)
                        else Color(0xFFFAFAFA))
                    .border(2.dp,
                        if (on) weapon.color.copy(alpha = 0.35f) else BorderBase,
                        RoundedCornerShape(20.dp))
                    .clickable {
                        onToggle(weapon.id, !on)
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (on) weapon.color.copy(alpha = 0.15f)
                            else Color(0xFFF3F4F6))
                        .border(2.dp,
                            if (on) weapon.color.copy(alpha = 0.2f)
                            else Color.Transparent,
                            RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(weapon.icon, fontSize = 22.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(weapon.name, fontSize = 14.sp, fontWeight = FontWeight.Black,
                        color = if (on) TextMain else Color(0xFF374151))
                    Text(weapon.desc, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                }
                Spacer(Modifier.width(14.dp))
                Switch(
                    checked = on,
                    onCheckedChange = { newState -> onToggle(weapon.id, newState) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = weapon.color,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE5E7EB)
                    )
                )
            }
        }

        if (activeCount >= 3) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = BorderStroke(2.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 22.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("¡Modo defensa activado! $activeCount armas encendidas.",
                        fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF065F46))
                }
            }
        }
    }
}