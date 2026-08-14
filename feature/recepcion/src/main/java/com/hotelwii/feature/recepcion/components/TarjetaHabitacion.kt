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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🛌 TarjetaHabitacion.kt — Card Smart de Altura Uniforme para Recepcionistas:
 * - Altura exacta uniforme (194.dp).
 * - Right top badge exclusivo: "LIBRE", "OCUPADO", "LIMPIEZA", "MANTENIMIENTO".
 * - Centro: Contador de tiempo en vivo dinámico (03h 45m 12s) para ocupadas + detalles de hospedaje (piso, baño, huésped).
 * - Bottom: Tarifa S/ y Capacidad.
 */
@Composable
fun TarjetaHabitacion(
    habitacion: ModeloHabitacion,
    ventaActiva: ModeloVenta? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoLower = habitacion.estado.lowercase()

    val (estadoColor, estadoIcono, estadoTexto) = when (estadoLower) {
        "disponible" -> Triple(WiCss.success, Icons.Rounded.CheckCircle, "LIBRE")
        "ocupada" -> Triple(WiCss.error, Icons.Rounded.Home, "OCUPADO")
        "limpieza" -> Triple(Color(0xFFD97706), WiIcons.CleaningServices, "LIMPIEZA")
        "mantenimiento" -> Triple(Color(0xFF6B7280), WiIcons.Build, "MANTENIMIENTO")
        else -> Triple(WiCss.mco, Icons.Rounded.Home, habitacion.estado.uppercase())
    }

    // Cronómetro en Vivo para Cuartos Ocupados (Contando segundos dinámicamente)
    var segundosTranscurridos by remember(habitacion.id, habitacion.estado, ventaActiva?.fechaIngreso) {
        val initialSeconds = calcularSegundosIniciales(ventaActiva?.fechaIngreso)
        mutableStateOf(initialSeconds)
    }

    LaunchedEffect(estadoLower) {
        if (estadoLower == "ocupada") {
            while (true) {
                delay(1000L)
                segundosTranscurridos += 1
            }
        }
    }

    val horas = segundosTranscurridos / 3600
    val minutos = (segundosTranscurridos % 3600) / 60
    val segs = segundosTranscurridos % 60
    val tiempoFormateado = String.format("%02dh %02dm %02ds", horas, minutos, segs)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(194.dp) // Mismo tamaño uniforme para todas las tarjetas
            .clip(RoundedCornerShape(22.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.brd, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 🏷️ Fila 1 (Top): Número Grande (101) + Right Top Badge (LIBRE, OCUPADO, LIMPIEZA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habitacion.numero,
                    style = WiText.h2,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Right Top Badge Puro
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(estadoColor.copy(alpha = 0.12f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = estadoIcono,
                            contentDescription = null,
                            tint = estadoColor,
                            modifier = Modifier.size(12.dp)
                        )
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
            }

            Spacer(Modifier.height(6.dp))

            // ⏱️ Fila 2 (Centro): Información Centralizada + Contador de Tiempo en Vivo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (estadoLower == "ocupada") {
                    // Tiempo Grande Contando en el Centro
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = WiIcons.Timer,
                            contentDescription = null,
                            tint = WiCss.error,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = tiempoFormateado,
                            style = WiText.body,
                            color = WiCss.error,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(3.dp))

                    Text(
                        text = if (!ventaActiva?.clienteNombre.isNullOrBlank())
                            "Huésped: ${ventaActiva?.clienteNombre}"
                        else "${habitacion.piso} • ${habitacion.tipo}",
                        style = WiText.small,
                        color = WiCss.tx3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (habitacion.conBano) "Con Baño Privado" else "Baño Compartido",
                        fontSize = 11.sp,
                        color = WiCss.tx4,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (estadoLower == "limpieza") {
                    Text(
                        text = "En Aseo y Desinfección",
                        style = WiText.body,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "${habitacion.piso} • ${habitacion.tipo}",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                } else {
                    // Estado LIBRE / DISPONIBLE
                    Text(
                        text = "${habitacion.piso} • ${habitacion.tipo}",
                        style = WiText.body,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "${if (habitacion.conBano) "Baño Privado" else "Baño Compartido"}${if (habitacion.conTv) " • Smart TV" else ""}",
                        style = WiText.small,
                        color = WiCss.tx3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // 💰 Fila 3 (Bottom): Tarifa Destacada + Capacidad de Huéspedes
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
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(13.dp)
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

/**
 * Calcula los segundos transcurridos desde fechaIngreso ISO o provee offset demo.
 */
private fun calcularSegundosIniciales(fechaIngresoIso: String?): Long {
    if (fechaIngresoIso.isNullOrBlank()) {
        return 13524L // 03h 45m 24s por defecto
    }
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val fecha = sdf.parse(fechaIngresoIso)
        if (fecha != null) {
            val diffMs = Date().time - fecha.time
            (diffMs / 1000L).coerceAtLeast(0L)
        } else {
            13524L
        }
    } catch (e: Exception) {
        13524L
    }
}
