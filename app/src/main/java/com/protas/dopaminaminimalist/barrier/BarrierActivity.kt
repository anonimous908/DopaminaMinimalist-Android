package com.protas.dopaminaminimalist.barrier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.protas.dopaminaminimalist.ui.theme.DopaminaMinimalistTheme

class BarrierActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto hace que la actividad no se pueda cerrar con "Atrás" fácilmente
        // y se muestre sobre todo.
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        setContent {
            DopaminaMinimalistTheme {
                BarrierScreen() // Tu pantalla de "Mantener 10s"
            }
        }
    }

    // Opcional: Deshabilitar botón atrás físico
    //override fun onBackPressed() {
        // No hacer nada para obligar a usar el botón de 10s
    //}
}