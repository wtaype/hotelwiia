package com.hotelwii.core.kidev

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.FzSmart
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kicss.dpSmart
import com.hotelwii.core.kicss.fPoppins

/**
 * 🎨 WiButtonVariant — Variantes de diseño estándar reutilizables para HotelWii.
 */
enum class WiButtonVariant {
    Primary,   // Gradiente de marca (mco -> hva) con texto e ícono txa (Blanco)
    Secondary, // Fondo secundario (inp) con texto e ícono de alto contraste tx5
    Outline,   // Sin fondo, borde sutil brd con texto e ícono de acento mco
    Error      // Fondo semáforo error (#FF3849) con texto e ícono blanco para acciones destructivas/limpieza
}

/**
 * 🔘 GoldPill — Píldora de estado en tono dorado/acento con protección antidesbordamiento.
 */
@Composable
fun GoldPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(WiCss.mco.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text.uppercase(),
            style = WiText.label,
            color = WiCss.mco,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

/**
 * 🔘 WiButton — Componente Atómico Reutilizable de Botón con Variantes Semánticas y Contraste `tx5`.
 */
@Composable
fun WiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: WiButtonVariant = WiButtonVariant.Primary,
    icon: ImageVector? = null,
    loading: Boolean = false,
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val alpha = if (loading) 0.5f else 1.0f

    // 🎨 Resolución de Color de Fondo
    val effectiveContainerColor = containerColor ?: when (variant) {
        WiButtonVariant.Primary -> null // Usa gradiente mco -> hva
        WiButtonVariant.Secondary -> WiCss.inp
        WiButtonVariant.Outline -> Color.Transparent
        WiButtonVariant.Error -> WiCss.error
    }

    val backgroundModifier = if (effectiveContainerColor != null) {
        Modifier.background(effectiveContainerColor.copy(alpha = alpha))
    } else {
        Modifier.background(
            Brush.linearGradient(
                listOf(
                    WiCss.mco.copy(alpha = alpha),
                    WiCss.hva.copy(alpha = alpha)
                )
            )
        )
    }

    // ✏️ Resolución de Color de Texto e Ícono (tx5 / txa / mco / white)
    val resolvedContentColor = contentColor ?: when {
        containerColor != null -> if (containerColor == WiCss.inp || containerColor == WiCss.wb) WiCss.tx5 else WiCss.txa
        variant == WiButtonVariant.Primary -> WiCss.txa
        variant == WiButtonVariant.Secondary -> WiCss.tx5
        variant == WiButtonVariant.Outline -> WiCss.mco
        variant == WiButtonVariant.Error -> WiCss.white
        else -> WiCss.txa
    }

    // 🔲 Borde Opcional para variante Outline
    val borderModifier = if (variant == WiButtonVariant.Outline && containerColor == null) {
        Modifier.border(BorderStroke(1.dp, WiCss.brd), RoundedCornerShape(18.dp))
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .then(borderModifier)
            .then(backgroundModifier)
            .clickable(enabled = !loading, onClick = onClick)
            .padding(horizontal = dpSmart(15f, 1.6f, 20f), vertical = dpSmart(8f, 1.0f, 12f)),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = resolvedContentColor, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(icon, null, tint = resolvedContentColor, modifier = Modifier.size(FzSmart.buttonIcon))
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = resolvedContentColor,
                    fontFamily = fPoppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FzSmart.button,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
