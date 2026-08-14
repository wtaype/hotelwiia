package com.hotelwii.feature.recepcion.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.components.Deslizable
import com.hotelwii.feature.recepcion.components.TarjetaHabitacion
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🏨 Habitaciones.kt — Sub-Pestaña 1: Rack Directo de Habitaciones 2x2 con Nav Bottom Compacto ("Unir" / "Nuevo").
 */
@Composable
fun Habitaciones(
    uiState: RecepcionUiState,
    onSeleccionarHabitacion: (ModeloHabitacion) -> Unit,
    onNuevaHabitacion: () -> Unit
) {
    var mostrarModalUnirHabitaciones by remember { mutableStateOf(false) }
    val habitacionesSeleccionadasParaUnir = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 86.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Grilla Directa 2x2 de Habitaciones con Altura Uniforme y Sincronización de Ventas Activas
            if (uiState.habitaciones.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(WiCss.wb)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay habitaciones registradas. Pulsa '+ Nuevo' para empezar.",
                        style = WiText.body,
                        color = WiCss.tx3
                    )
                }
            } else {
                val parejas = uiState.habitaciones.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    parejas.forEach { pareja ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Habitación 1
                            val hab1 = pareja[0]
                            val venta1 = uiState.ventasActivas.find { it.habitacionId == hab1.id || it.habitacionId == hab1.numero }
                            Box(modifier = Modifier.weight(1f)) {
                                TarjetaHabitacion(
                                    habitacion = hab1,
                                    ventaActiva = venta1,
                                    onClick = { onSeleccionarHabitacion(hab1) }
                                )
                            }

                            // Habitación 2
                            if (pareja.size > 1) {
                                val hab2 = pareja[1]
                                val venta2 = uiState.ventasActivas.find { it.habitacionId == hab2.id || it.habitacionId == hab2.numero }
                                Box(modifier = Modifier.weight(1f)) {
                                    TarjetaHabitacion(
                                        habitacion = hab2,
                                        ventaActiva = venta2,
                                        onClick = { onSeleccionarHabitacion(hab2) }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // ⚓ Nav Bottom Fijo: Botones Compactos ("Unir" / "Nuevo")
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Unir
                Box(modifier = Modifier.weight(1f)) {
                    WiButton(
                        text = "Unir",
                        onClick = { mostrarModalUnirHabitaciones = true },
                        variant = WiButtonVariant.Secondary,
                        icon = WiIcons.Link,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Botón Nuevo (Texto Corto)
                Box(modifier = Modifier.weight(1.2f)) {
                    WiButton(
                        text = "Nuevo",
                        onClick = onNuevaHabitacion,
                        variant = WiButtonVariant.Primary,
                        icon = Icons.Rounded.Add,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Modal Deslizable para Unir Habitaciones
        if (mostrarModalUnirHabitaciones) {
            Deslizable(
                onCerrar = { mostrarModalUnirHabitaciones = false },
                titulo = "Unir Habitaciones para Grupos",
                subtitulo = "Selecciona 2 o más cuartos para agruparlos bajo un mismo titular",
                icono = WiIcons.Link
            ) {
                Text(
                    text = "Selecciona las habitaciones que ocupará el grupo:",
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.habitaciones.forEach { hab ->
                        val estaSeleccionada = habitacionesSeleccionadasParaUnir.contains(hab.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (estaSeleccionada) WiCss.mco.copy(alpha = 0.12f) else WiCss.inp)
                                .clickable {
                                    if (hab.id != null) {
                                        if (estaSeleccionada) habitacionesSeleccionadasParaUnir.remove(hab.id)
                                        else habitacionesSeleccionadasParaUnir.add(hab.id)
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Hab. ${hab.numero} (${hab.tipo})",
                                    style = WiText.body,
                                    color = WiCss.tx1,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Piso: ${hab.piso} • S/ ${String.format("%.2f", hab.precio)}",
                                    style = WiText.small,
                                    color = WiCss.tx3
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (estaSeleccionada) WiCss.mco else WiCss.wb)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiButton(
                        text = "Cancelar",
                        onClick = { mostrarModalUnirHabitaciones = false },
                        variant = WiButtonVariant.Cancel,
                        modifier = Modifier.weight(1f)
                    )

                    WiButton(
                        text = "Confirmar Unión (${habitacionesSeleccionadasParaUnir.size})",
                        onClick = { mostrarModalUnirHabitaciones = false },
                        variant = WiButtonVariant.Primary,
                        icon = WiIcons.Link,
                        modifier = Modifier.weight(1.4f)
                    )
                }
            }
        }
    }
}
