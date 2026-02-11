package com.protas.dopaminaminimalist.presentation

import android.app.Activity
import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BarrierScreen() {
    val context = LocalContext.current
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }

    // Lógica del Timer: Si mantiene pulsado, sube el progreso
    LaunchedEffect(isHolding) {
        if (isHolding) {
            val duration = 10000 // 10 segundos
            val steps = 100
            val delayTime = duration / steps.toLong()

            for (i in 1..steps) {
                progress = i / 100f
                delay(delayTime)
            }
            // Si llega al 100%, CERRAMOS la barrera (dejamos pasar al usuario)
            (context as? Activity)?.finish()
        } else {
            // Si suelta, reseteamos a 0
            progress = 0f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f)), // Fondo casi opaco
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "ALTO AHÍ",
                color = Color.Red,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tu IA detectó un nivel de vicio del 100%.\n¿Realmente necesitas entrar aquí?",
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // EL BOTÓN DE "MANTENER"
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Red,
                    strokeWidth = 8.dp,
                )

                Button(
                    onClick = { /* No hace nada al click simple */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                    modifier = Modifier
                        .size(180.dp)
                        .pointerInteropFilter { motionEvent ->
                            when (motionEvent.action) {
                                MotionEvent.ACTION_DOWN -> isHolding = true
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isHolding = false
                            }
                            true
                        },
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Text(if (isHolding) "RESISTE..." else "MANTÉN 10s\nPARA ENTRAR", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
