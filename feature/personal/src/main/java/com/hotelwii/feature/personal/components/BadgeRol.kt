package com.hotelwii.feature.personal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons

/**
 * 🏷️ BadgeRol.kt — Badge visual para el rol del colaborador.
 */
@Composable
fun BadgeRol(
    rol: String,
    modifier: Modifier = Modifier
) {
    val (colorFondo, colorTexto, texto, icono) = when (rol.lowercase().trim()) {
        "admin", "administrador" -> Quad(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFF7C3AED), "Administrador", WiIcons.Lock)
        "recepcion", "recepcionista" -> Quad(WiCss.mco.copy(alpha = 0.15f), WiCss.mco, "Recepción", WiIcons.Building)
        "limpieza", "housekeeping" -> Quad(Color(0xFFF59E0B).copy(alpha = 0.15f), Color(0xFFD97706), "Limpieza", WiIcons.Refresh)
        "caja", "cajero" -> Quad(WiCss.success.copy(alpha = 0.15f), WiCss.success, "Caja", WiIcons.Receipt)
        else -> Quad(WiCss.inp, WiCss.tx2, rol.replaceFirstChar { it.uppercase() }, WiIcons.Person)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorTexto,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = texto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
