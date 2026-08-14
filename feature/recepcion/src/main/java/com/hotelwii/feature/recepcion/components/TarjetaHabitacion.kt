package com.hotelwii.feature.recepcion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🛌 TarjetaHabitacion.kt — Card Smart 2x2 para Recepcionistas: Número Gigante 101, Íconos de Estado, Timer de Ocupación (03h 45m) y Cero Amenidades Saturadas.
 */
@Composable
fun TarjetaHabitacion(
    habitacion: ModeloHabitacion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (estadoColor, estadoIcono, estadoTexto) = when (habitacion.estado.lowercase()) {
        "disponible" -> Triple(WiCss.success, Icons.Rounded.CheckCircle, "DISPONIBLE")
        "ocupada" -> Triple(WiCss.error, Icons.Rounded.Home, "03h 45m") // Timer de ocupación transcurrido
        "limpieza" -> Triple(Color(0xFFD97706), WiIcons.CleaningServices, "LIMPIEZA")
        "mantenimiento" -> Triple(Color(0xFF6B7280), WiIcons.Build, "MANTENIMIENTO")
        else -> Triple(WiCss.mco, Icons.Rounded.Home, habitacion.estado.uppercase())
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.brd, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header: Número Gigante (101) + Ícono / Timer de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(estadoColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = estadoIcono,
                            contentDescription = null,
                            tint = estadoColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = habitacion.numero,
                        style = WiText.h3,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(4.dp))

                // Badge de Estado / Timer Transcurrido (03h 45m) en Ocupada
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(estadoColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = estadoTexto,
                        fontSize = 10.sp,
                        color = estadoColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = "${habitacion.piso} • ${habitacion.tipo}",
                style = WiText.small,
                color = WiCss.tx3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Precio Destacado + Capacidad (Cero Amenidades para no saturar el Rack)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "S/ ${String.format("%.2f", habitacion.precio)}",
                    style = WiText.h4,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WiCss.inp)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${habitacion.capacidad}",
                        style = WiText.small,
                        color = WiCss.tx2,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
