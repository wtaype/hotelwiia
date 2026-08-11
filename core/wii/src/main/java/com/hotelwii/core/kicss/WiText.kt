package com.hotelwii.core.kicss

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * WiText — Escala tipográfica del sistema de diseño HotelWii.
 * Usa fPoppins definido en Estilos.kt y las dimensiones de FzSmart.kt
 */
object WiText {
    val h1        @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Bold,     fontSize = FzSmart.fz_l2)
    val h2        @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Bold,     fontSize = FzSmart.fz_l1)
    val h3        @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.SemiBold, fontSize = FzSmart.fz_m4)
    val h4        @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.SemiBold, fontSize = FzSmart.fz_m1)
    val body      @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium,   fontSize = FzSmart.fz_m)
    val small     @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium,   fontSize = FzSmart.fz_s3)
    val tiny      @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.Medium,   fontSize = FzSmart.fz_s1)
    val label     @Composable get() = TextStyle(fontFamily = fPoppins, fontWeight = FontWeight.SemiBold, fontSize = FzSmart.fz_s2)

    // 🧾 Estilos especializados POS / Facturación de Caja con fOutfit
    val posAmount @Composable get() = TextStyle(fontFamily = fOutfit,  fontWeight = FontWeight.Bold,     fontSize = FzSmart.fz_l2)
    val posPrice  @Composable get() = TextStyle(fontFamily = fOutfit,  fontWeight = FontWeight.SemiBold, fontSize = FzSmart.fz_m4)
    val posCode   @Composable get() = TextStyle(fontFamily = fOutfit,  fontWeight = FontWeight.Medium,   fontSize = FzSmart.fz_s3)
}
