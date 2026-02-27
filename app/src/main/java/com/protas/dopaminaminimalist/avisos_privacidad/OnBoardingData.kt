package com.protas.dopaminaminimalist.avisos_privacidad

import androidx.compose.ui.graphics.Color
import com.protas.dopaminaminimalist.R


data class OnBoardingData (
    val titulo: String,
    val descripcion: String,
    val imagen: Int, // El ID de tu drawable (R.drawable...)
    val colorFondo: androidx.compose.ui.graphics.Color
)
val listaPaginas = listOf(
    OnBoardingData(
        titulo = "Bienvenido a enfocaAPP",
        descripcion = "Tu aliado inteligente para recuperar el control de tu tiempo.",
        imagen = R.drawable.logo_reloj,
        colorFondo = Color(0xFFE3F2FD) // Un azul muy claro
    ),
    OnBoardingData(
        titulo = "IA a tu servicio",
        descripcion = "Usamos tecnologia de aprendizaje automatico TensorFlow Lite para analizar tus patrones de uso pero no te preocupes no recolectamos datos ya que nos importa tu privacidad.",
        imagen = R.drawable.ia_analisis,
        colorFondo = Color(0xFFF1F8E9) // Un verde muy claro
    ),
    OnBoardingData(
        titulo = "Metas Reales",
        descripcion = "Establece límites y recibe notificaciones cuando tu cerebro necesite un respiro.",
        imagen = R.drawable.metas_icon,
        colorFondo = Color(0xFFFFF3E0) // Un naranja muy claro
    ),
    OnBoardingData(
        titulo = "Privacidad y Seguridad",
        descripcion = "En Ztrene Studios protegemos tus datos. El análisis de IA es 100% local y privado. Al continuar, aceptas nuestra política.",
        imagen = R.drawable.logo_reloj, // Deberás crear este icono
        colorFondo = Color(color = 0xFFE0F7FA) // Un azul/cian muy claro
    )
)