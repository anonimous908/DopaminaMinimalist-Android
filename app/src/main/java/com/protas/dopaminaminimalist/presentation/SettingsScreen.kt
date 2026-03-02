package com.protas.dopaminaminimalist.presentation



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de datos para tus interruptores
data class DefenseOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    var isEnabled: Boolean = false
)

@Composable
fun SettingsScreen() {
    // ESTADO: Aquí guardamos si los switches están activos
    // (En producción usarías DataStore o ViewModel)
    val options = remember { mutableStateListOf(
        DefenseOption("notify", "👁️ Vigilancia Activa", "Notificar cada 5 min de uso", Icons.Default.Notifications),
        DefenseOption("barrier", "🚧 Barrera de 10s", "Mantener pulsado para entrar", Icons.Default.Lock),
        DefenseOption("grayscale", "🌑 Modo Grises", "Hacer la pantalla aburrida", Icons.Default.BrightnessMedium),
        DefenseOption("monk", "🧘 Modo Monje", "Bloqueo total sin piedad", Icons.Default.SelfImprovement),
        DefenseOption("aggressive", "💬 IA Pasivo-Agresiva", "Mensajes que duelen", Icons.Default.Psychology),
        DefenseOption("stats", "📊 HUD en Pantalla", "Ver contador flotante", Icons.Default.Analytics),
        DefenseOption("night", "🌙 Toque de Queda", "Bloqueo automático 11 PM", Icons.Default.Nightlight),
        DefenseOption("kill", "💀 Muerte Súbita", "Cerrar app a la fuerza", Icons.Default.Dangerous)
    )}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Fondo Cyberpunk Oscuro
            .padding(16.dp)
    ) {
        Text(
            text = "Sistema De Guerra A La Dopamina Barata",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(options) { option ->
                DefenseCard(option) { newState ->
                    // Aquí actualizamos el estado y activamos/desactivamos el servicio
                    val index = options.indexOf(option)
                    options[index] = option.copy(isEnabled = newState)

                    // TODO: Aquí llamarías a tu ViewModel o Service
                    // if (option.id == "notify" && newState) startService()
                }
            }
        }
    }
}

@Composable
fun DefenseCard(option: DefenseOption, onToggle: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                tint = if (option.isEnabled) Color(0xFFFF5252) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = option.description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Switch(
                checked = option.isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFFF5252),
                    checkedTrackColor = Color(0xFF5D1010)
                )
            )
        }
    }
}