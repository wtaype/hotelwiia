package com.hotelwii.core.kicss

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.hotelwii.core.wii.R

// ─────────────────────────────────────────────────────────────────────────
// 🔤 Familias tipográficas (fPoppins / fOutfit)
// ─────────────────────────────────────────────────────────────────────────
val fPoppins = FontFamily(
    Font(R.font.poppins_medium,   FontWeight.Medium),   // 500
    Font(R.font.poppins_semibold, FontWeight.SemiBold), // 600
    Font(R.font.poppins_bold,     FontWeight.Bold),     // 700
)
val ff_P = fPoppins

val fOutfit = FontFamily(
    Font(R.font.outfit_medium,   FontWeight.Medium),   // 500
    Font(R.font.outfit_semibold, FontWeight.SemiBold), // 600
    Font(R.font.outfit_bold,     FontWeight.Bold),     // 700
)
val ff_O = fOutfit

// ─────────────────────────────────────────────────────────────────────────
// 🎨 Contexto Global de Tema (CompositionLocalProvider + Dynamic System Bar)
// ─────────────────────────────────────────────────────────────────────────
val LocalWiTemaColors = staticCompositionLocalOf { temaPorDefecto() }

/**
 * WiTemaApp — Proveedor del tema activo. Sincroniza dinámicamente el color
 * de la barra de estado (StatusBar) y barra de navegación con el tema activo.
 */
@Composable
fun WiTemaApp(
    themeColors: WiTemaColors = temaPorDefecto(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Color dinámico de la StatusBar usando themeColors.wb y NavigationBar usando themeColors.bg
                window.statusBarColor     = themeColors.wb.toArgb()
                window.navigationBarColor = themeColors.bg.toArgb()

                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars     = !themeColors.isDark
                insetsController.isAppearanceLightNavigationBars = !themeColors.isDark
            }
        }
    }

    CompositionLocalProvider(LocalWiTemaColors provides themeColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors.bg)
        ) {
            content()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// 🖌️ WiCss — Acceso 1:1 a las 27 variables CSS del tema activo
// ─────────────────────────────────────────────────────────────────────────
object WiCss {
    // ─── Las 27 variables de tema (1:1 de witema.css) ─────────────────
    val bg        @Composable get() = LocalWiTemaColors.current.bg
    val wb        @Composable get() = LocalWiTemaColors.current.wb
    val tx        @Composable get() = LocalWiTemaColors.current.tx
    val tx1       @Composable get() = LocalWiTemaColors.current.tx1
    val tx2       @Composable get() = LocalWiTemaColors.current.tx2
    val tx3       @Composable get() = LocalWiTemaColors.current.tx3
    val tx4       @Composable get() = LocalWiTemaColors.current.tx4
    val tx5       @Composable get() = LocalWiTemaColors.current.tx5
    val txa       @Composable get() = LocalWiTemaColors.current.txa
    val txe       @Composable get() = LocalWiTemaColors.current.txe
    val hv        @Composable get() = LocalWiTemaColors.current.hv
    val hva       @Composable get() = LocalWiTemaColors.current.hva
    val mco       @Composable get() = LocalWiTemaColors.current.mco
    val mbg       @Composable get() = LocalWiTemaColors.current.mbg
    val brd       @Composable get() = LocalWiTemaColors.current.brd
    val inp       @Composable get() = LocalWiTemaColors.current.inp
    val bg1       @Composable get() = LocalWiTemaColors.current.bg1
    val bg2       @Composable get() = LocalWiTemaColors.current.bg2
    val bg3       @Composable get() = LocalWiTemaColors.current.bg3
    val bg4       @Composable get() = LocalWiTemaColors.current.bg4
    val bg5       @Composable get() = LocalWiTemaColors.current.bg5
    val bg6       @Composable get() = LocalWiTemaColors.current.bg6
    val bg7       @Composable get() = LocalWiTemaColors.current.bg7
    val bg8       @Composable get() = LocalWiTemaColors.current.bg8
    val bt        @Composable get() = LocalWiTemaColors.current.bt
    val glassBg   @Composable get() = LocalWiTemaColors.current.glassBg
    val glassBrd  @Composable get() = LocalWiTemaColors.current.glassBrd
    val glassShd  @Composable get() = LocalWiTemaColors.current.glassShd
    val glassBlur @Composable get() = LocalWiTemaColors.current.glassBlur
    val isDark    @Composable get() = LocalWiTemaColors.current.isDark

    // ─── Variables globales del :root ─────────────────────────────────
    val success  get() = WiTemaGlobal.success
    val error    get() = WiTemaGlobal.error
    val warning  get() = WiTemaGlobal.warning
    val info     = WiTemaGlobal.info
    val white    = WiTemaGlobal.white
    val offline  = WiTemaGlobal.offline

    // ─── Helpers Glassmorphism ────────────────────────────────────────
    @Composable
    fun glassShape(intensity: Float = 0.55f): RoundedCornerShape =
        RoundedCornerShape((12f + 14f * intensity.coerceIn(0f, 1f)).dp)

    @Composable
    fun glassColors(intensity: Float = 0.55f): CardColors =
        CardDefaults.cardColors(containerColor = glassBg)

    @Composable
    fun glassBorder(intensity: Float = 0.55f): BorderStroke =
        BorderStroke(1.dp, glassBrd)
}
