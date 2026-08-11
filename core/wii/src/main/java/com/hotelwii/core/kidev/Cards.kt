package com.hotelwii.core.kidev

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hotelwii.core.kicss.FzSmart
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.softGlassShadow

/**
 * 🃏 Cards.kt — Tarjetas de Diseño Glassmorphism de HotelWii.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    intensity: Float = 0.55f,
    shape: androidx.compose.ui.graphics.Shape? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val finalShape = shape ?: WiCss.glassShape(intensity)
    val cardContent: @Composable () -> Unit = {
        Column(Modifier.padding(FzSmart.cardPad)) {
            content()
        }
    }
    val cardModifier = modifier
        .softGlassShadow(shape = finalShape)
        .clip(finalShape)

    if (onClick == null) {
        Card(
            modifier = cardModifier,
            shape = finalShape,
            colors = WiCss.glassColors(intensity),
            border = WiCss.glassBorder(intensity),
            content = { cardContent() },
        )
    } else {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = finalShape,
            colors = WiCss.glassColors(intensity),
            border = WiCss.glassBorder(intensity),
            content = { cardContent() },
        )
    }
}
