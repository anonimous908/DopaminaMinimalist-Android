package com.protas.dopaminaminimalist.ui.navigation


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNav(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        Triple("home",     "🏠", "Inicio"),
        Triple("armas",    "🛡️", "Retos"),
        Triple("progreso", "📈", "Progreso")
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardBg,
        shadowElevation = 8.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            tabs.forEach { (id, icon, label) ->
                val isSelected = selected == id
                Column(
                    modifier = Modifier.weight(1f).clickable { onSelect(id) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) Color(0xFFEEF2FF)
                                else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(icon, fontSize = 20.sp)
                    }
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color(0xFF4F46E5) else TextMuted
                    )
                }
            }
        }
    }
}