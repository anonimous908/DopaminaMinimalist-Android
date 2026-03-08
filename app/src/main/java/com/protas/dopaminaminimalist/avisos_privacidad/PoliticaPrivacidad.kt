package com.protas.dopaminaminimalist.avisos_privacidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//Información de privacidad
@Composable
fun PoliticaPrivacidadDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Privacidad - enfocaAPP") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = """
                        Ztrene Studios - enfocaAPP
                        
                        1. PROCESAMIENTO LOCAL: Toda la información analizada por nuestra IA (TensorFlow Lite) ocurre 100% local en tu dispositivo. No recolectamos datos personales.
                        
                        2. ESTADÍSTICAS DE USO: Requerimos permiso para analizar los tiempos de uso de tus aplicaciones para generar las gráficas de bienestar digital.
                        
                        3. SIN NUBE: Tus hábitos no se envían a servidores externos. La privacidad es nuestro pilar fundamental.
                        
                        Contacto: ztrenestudios@gmail.com
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}