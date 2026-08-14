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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.hotelwii.core.kidev.WiField
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.components.Deslizable
import com.hotelwii.feature.recepcion.components.TarjetaHabitacion
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🏨 Habitaciones.kt — Sub-Pestaña 1: Rack Pro de Habitaciones con Conmutador por Íconos, Buscador (60%) + Filtro (40%) y Footer Fijo.
 */
@Composable
fun Habitaciones(
    uiState: RecepcionUiState,
    onSeleccionarHabitacion: (ModeloHabitacion) -> Unit,
    onNuevaHabitacion: () -> Unit
) {
    var filtroBusqueda by remember { mutableStateOf("") }
    var estadoFiltro by remember { mutableStateOf("todas") }
    var vistaModoCards by remember { mutableStateOf(true) }

    var dropdownEstadoExpandido by remember { mutableStateOf(false) }

    var mostrarModalUnirHabitaciones by remember { mutableStateOf(false) }
    val habitacionesSeleccionadasParaUnir = remember { mutableStateListOf<String>() }

    val habitacionesFiltradas = uiState.habitaciones.filter { hab ->
        val coincideTexto = hab.numero.contains(filtroBusqueda, ignoreCase = true) ||
                hab.piso.contains(filtroBusqueda, ignoreCase = true) ||
                hab.tipo.contains(filtroBusqueda, ignoreCase = true)
        val coincideEstado = if (estadoFiltro == "todas") true else hab.estado.equals(estadoFiltro, ignoreCase = true)
        coincideTexto && coincideEstado
    }

    val opcionesEstado = listOf("Todas", "Disponibles", "Ocupadas", "Limpieza", "Mantenimiento")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Compacto Pro: Título + Conmutador Solo Íconos (Grid 2x2 vs List)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiCss.wb)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rack de Habitaciones (${uiState.habitaciones.size})",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )

                        // Conmutador Solo Íconos
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (vistaModoCards) WiCss.mco.copy(alpha = 0.15f) else WiCss.inp)
                                    .clickable { vistaModoCards = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = WiIcons.GridView,
                                    contentDescription = "Vista Cuadro 2x2",
                                    tint = if (vistaModoCards) WiCss.mco else WiCss.tx3,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!vistaModoCards) WiCss.mco.copy(alpha = 0.15f) else WiCss.inp)
                                    .clickable { vistaModoCards = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = WiIcons.ViewList,
                                    contentDescription = "Vista Lista",
                                    tint = if (!vistaModoCards) WiCss.mco else WiCss.tx3,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Fila Combinada Pro: Buscador con Placeholder Corto + Dropdown Menu Flotante (Popup)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Buscador con placeholder corto "Buscar..."
                        Box(modifier = Modifier.weight(1.5f)) {
                            WiField(
                                value = filtroBusqueda,
                                onValueChange = { filtroBusqueda = it },
                                label = "Buscar...",
                                leadingIcon = Icons.Rounded.Search,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Selector de Estado con DropdownMenu Flotante por Encima de la UI (Cero Deformación)
                        Box(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(WiCss.inp)
                                    .clickable { dropdownEstadoExpandido = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = when (estadoFiltro) {
                                            "disponible" -> "Disponibles"
                                            "ocupada" -> "Ocupadas"
                                            "limpieza" -> "Limpieza"
                                            "mantenimiento" -> "Mantenimiento"
                                            else -> "Todas"
                                        },
                                        style = WiText.body,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = null,
                                        tint = WiCss.tx3
                                    )
                                }
                            }

                            // Popup Flotante Overlay
                            DropdownMenu(
                                expanded = dropdownEstadoExpandido,
                                onDismissRequest = { dropdownEstadoExpandido = false },
                                modifier = Modifier.background(WiCss.wb)
                            ) {
                                opcionesEstado.forEach { op ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = op,
                                                style = WiText.body,
                                                color = WiCss.tx1,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        onClick = {
                                            estadoFiltro = when (op) {
                                                "Disponibles" -> "disponible"
                                                "Ocupadas" -> "ocupada"
                                                "Limpieza" -> "limpieza"
                                                "Mantenimiento" -> "mantenimiento"
                                                else -> "todas"
                                            }
                                            dropdownEstadoExpandido = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Grilla de 2 Columnas (2 por fila / 2 en 2) o Lista
            if (habitacionesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(WiCss.wb)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron habitaciones con los filtros aplicados.",
                        style = WiText.body,
                        color = WiCss.tx3
                    )
                }
            } else if (vistaModoCards) {
                val parejas = habitacionesFiltradas.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    parejas.forEach { pareja ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                TarjetaHabitacion(
                                    habitacion = pareja[0],
                                    onClick = { onSeleccionarHabitacion(pareja[0]) }
                                )
                            }

                            if (pareja.size > 1) {
                                Box(modifier = Modifier.weight(1f)) {
                                    TarjetaHabitacion(
                                        habitacion = pareja[1],
                                        onClick = { onSeleccionarHabitacion(pareja[1]) }
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    habitacionesFiltradas.forEach { hab ->
                        TarjetaHabitacion(
                            habitacion = hab,
                            onClick = { onSeleccionarHabitacion(hab) }
                        )
                    }
                }
            }
        }

        // ⚓ Footer Fijo Pro (Nav Bottom Fijo) en la parte inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WiButton(
                    text = "Unir Habitaciones",
                    onClick = { mostrarModalUnirHabitaciones = true },
                    variant = WiButtonVariant.Secondary,
                    icon = WiIcons.Link,
                    modifier = Modifier.weight(1.2f)
                )

                WiButton(
                    text = "Nueva Habitación",
                    onClick = onNuevaHabitacion,
                    variant = WiButtonVariant.Primary,
                    icon = Icons.Rounded.Add,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Modal de Selección para Unir Habitaciones (Grupos / Familias de 10+ personas)
        if (mostrarModalUnirHabitaciones) {
            Deslizable(
                onCerrar = { mostrarModalUnirHabitaciones = false },
                titulo = "Unir Habitaciones para Grupos / Familias",
                subtitulo = "Selecciona 2 o más cuartos para registrarlos bajo un mismo titular",
                icono = WiIcons.Link
            ) {
                Text(
                    text = "Selecciona las habitaciones que ocupará la familia:",
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
                        variant = WiButtonVariant.Outline,
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
