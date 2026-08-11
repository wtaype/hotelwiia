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
// 🔠 FzSmart — Escala tipográfica y de espaciados fz_ (de witema.css)
// ─────────────────────────────────────────────────────────────────────────
object FzSmart {
    // Escalas de fuente CSS (TextUnit sp)
    val fz_s1: TextUnit get() = 11.sp   // --fz_s1: clamp(0.625rem, 0.8vh, 0.7rem)
    val fz_s2: TextUnit get() = 11.5.sp // --fz_s2: clamp(0.675rem, 0.85vh, 0.75rem)
    val fz_s3: TextUnit get() = 12.sp   // --fz_s3: clamp(0.725rem, 0.9vh, 0.8rem)
    val fz_s4: TextUnit get() = 13.sp   // --fz_s4: clamp(0.775rem, 1vh, 0.85rem)
    val fz_m:  TextUnit get() = 14.sp   // --fz_m:  clamp(0.8rem, 1.05vh, 0.9rem)
    val fz_m1: TextUnit get() = 15.sp   // --fz_m1: clamp(0.85rem, 1.1vh, 0.95rem)
    val fz_m2: TextUnit get() = 16.sp   // --fz_m2: clamp(0.9rem, 1.15vh, 1rem)
    val fz_m3: TextUnit get() = 17.sp   // --fz_m3: clamp(0.95rem, 1.2vh, 1.05rem)
    val fz_m4: TextUnit get() = 18.sp   // --fz_m4: clamp(1rem, 1.3vh, 1.1rem)
    val fz_m5: TextUnit get() = 20.sp   // --fz_m5: clamp(1.15rem, 1.5vh, 1.25rem)
    val fz_l1: TextUnit get() = 24.sp   // --fz_l1: clamp(1.5rem, 2vh, 1.7rem)
    val fz_l2: TextUnit get() = 28.sp   // --fz_l2: clamp(1.8rem, 2.3vh, 2rem)
    val fz_x1: TextUnit get() = 34.sp   // --fz_x1: clamp(2.2rem, 3vh, 2.8rem)
    val fz_x2: TextUnit get() = 40.sp   // --fz_x2: clamp(2.5rem, 3.6vh, 3.5rem)
    val fz_x3: TextUnit get() = 48.sp   // --fz_x3: clamp(2.9rem, 5vh, 3.9rem)
    val fz_x4: TextUnit get() = 56.sp   // --fz_x4: clamp(3.3rem, 5.9vh, 4.3rem)

    // Aliases funcionales de componentes
    val button: TextUnit get() = fz_m
    val body:   TextUnit get() = fz_m
    val field:  TextUnit get() = fz_s4
    val small:  TextUnit get() = fz_s3

    // Tamaños de icono y espaciados (Dp adaptativos)
    val buttonIcon @Composable get(): Dp = dpSmart(16f, 2.0f, 22f)
    val fieldIcon  @Composable get(): Dp = dpSmart(18f, 2.2f, 24f)
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
