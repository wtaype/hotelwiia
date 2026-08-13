package com.hotelwii.core.kidev

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.FzSmart
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kicss.dpSmart
import com.hotelwii.core.kicss.fPoppins

/**
 * 🔘 Buttons.kt — Botones y Pills Atómicos de HotelWii con contraste de color inteligente.
 */
@Composable
fun GoldPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(WiCss.mco.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text.uppercase(), style = WiText.label, color = WiCss.mco, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun WiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    containerColor: Color? = null,
    contentColor: Color? = null
) {
    val alpha = if (loading) 0.5f else 1.0f
    val backgroundModifier = if (containerColor != null) {
        Modifier.background(containerColor.copy(alpha = alpha))
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

    val resolvedContentColor = contentColor ?: if (containerColor == WiCss.inp || containerColor == WiCss.wb) WiCss.tx1 else WiCss.txa

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
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
                    fontSize = FzSmart.button
                )
            }
        }
    }
}
