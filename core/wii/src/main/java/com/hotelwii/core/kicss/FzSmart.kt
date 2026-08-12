package com.hotelwii.core.kicss

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────
// 📐 dpSmart / clampDp — Tamaño adaptativo al alto de pantalla
// ─────────────────────────────────────────────────────────────────────────
@Composable
fun dpSmart(min: Float, preferredVh: Float, max: Float): Dp {
    val screenH = LocalConfiguration.current.screenHeightDp.toFloat()
    return (screenH * preferredVh / 100f).coerceIn(min, max).dp
}

@Composable
fun clampDp(min: Float, preferredVh: Float, max: Float): Dp = dpSmart(min, preferredVh, max)

// ─────────────────────────────────────────────────────────────────────────
// 🔠 FzSmart — Escala tipográfica adaptativa con scaleFactor dinámico
// ─────────────────────────────────────────────────────────────────────────
object FzSmart {
    var scaleFactor: Float = 1.0f

    // Escalas de fuente CSS (TextUnit sp) multiplicadas dinámicamente por scaleFactor
    val fz_s1: TextUnit get() = (11f * scaleFactor).sp
    val fz_s2: TextUnit get() = (11.5f * scaleFactor).sp
    val fz_s3: TextUnit get() = (12f * scaleFactor).sp
    val fz_s4: TextUnit get() = (13f * scaleFactor).sp
    val fz_m:  TextUnit get() = (14f * scaleFactor).sp
    val fz_m1: TextUnit get() = (15f * scaleFactor).sp
    val fz_m2: TextUnit get() = (16f * scaleFactor).sp
    val fz_m3: TextUnit get() = (17f * scaleFactor).sp
    val fz_m4: TextUnit get() = (18f * scaleFactor).sp
    val fz_m5: TextUnit get() = (20f * scaleFactor).sp
    val fz_l1: TextUnit get() = (24f * scaleFactor).sp
    val fz_l2: TextUnit get() = (28f * scaleFactor).sp
    val fz_x1: TextUnit get() = (34f * scaleFactor).sp
    val fz_x2: TextUnit get() = (40f * scaleFactor).sp
    val fz_x3: TextUnit get() = (48f * scaleFactor).sp
    val fz_x4: TextUnit get() = (56f * scaleFactor).sp

    // Aliases funcionales de componentes
    val button: TextUnit get() = fz_m
    val body:   TextUnit get() = fz_m
    val field:  TextUnit get() = fz_s4
    val small:  TextUnit get() = fz_s3

    // Tamaños de icono y espaciados (Dp adaptativos)
    val buttonIcon @Composable get(): Dp = dpSmart(16f * scaleFactor, 2.0f, 24f * scaleFactor)
    val fieldIcon  @Composable get(): Dp = dpSmart(18f * scaleFactor, 2.2f, 26f * scaleFactor)
    val cardPad    @Composable get(): Dp = dpSmart(14f, 1.8f, 22f)
}

// ─────────────────────────────────────────────────────────────────────────
// 🌫️ softGlassShadow — Sombra suave para tarjetas glass
// ─────────────────────────────────────────────────────────────────────────
fun Modifier.softGlassShadow(
    elevation: Dp = 6.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp)
): Modifier = this.shadow(
    elevation   = elevation,
    shape       = shape,
    spotColor   = Color.Black.copy(alpha = 0.10f),
    ambientColor = Color.Black.copy(alpha = 0.05f),
)
