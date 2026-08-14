package com.hotelwii.feature.recepcion.tabs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🏷️ Precios.kt — Sub-Pestaña 2: Dropdowns Flotantes Pro + Botones Simétricos Idénticos (Editar / Eliminar).
 */
@Composable
fun Precios(
    uiState: RecepcionUiState,
    onNuevaHabitacion: () -> Unit,
    onEditarHabitacion: (ModeloHabitacion) -> Unit,
    onActualizarPrecioRapido: (String, Double) -> Unit,
    onEliminarHabitacion: (String) -> Unit
) {
    var habitacionEdicionRapidaPrecio by remember { mutableStateOf<ModeloHabitacion?>(null) }
    var nuevoPrecioInput by remember { mutableStateOf("") }

    // Inputs Fijos para Registro Rápido
    var nuevoNumero by remember { mutableStateOf("") }
    var nuevoPrecioStr by remember { mutableStateOf("") }
    var nuevoPiso by remember { mutableStateOf("Piso 1") }
    var nuevoTipo by remember { mutableStateOf("Matrimonial") }

    var dropdownPisoExpandido by remember { mutableStateOf(false) }
    var dropdownTipoExpandido by remember { mutableStateOf(false) }

    val opcionesPiso = listOf("Piso 1", "Piso 2", "Piso 3", "Piso 4", "Suites Dunas")
    val opcionesTipo = listOf("Simple", "Matrimonial", "Doble", "Triple", "Suite Jacuzzi")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Box Superior Fijo para Registro con Dropdowns Flotantes Pro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Registrar Nueva Habitación y Tarifa",
                    style = WiText.h4,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = nuevoNumero,
                        onValueChange = { nuevoNumero = it },
                        label = "Número (ej: 105)",
                        modifier = Modifier.weight(1f)
                    )

                    WiField(
                        value = nuevoPrecioStr,
                        onValueChange = { nuevoPrecioStr = it },
                        label = "Precio (S/)",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fila con DropdownMenu Flotantes (Cero Deformación)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dropdown Flotante: Piso
                    Box(modifier = Modifier.weight(1f)) {
                        Column {
                            Text(text = "Piso", style = WiText.small, color = WiCss.tx3)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(WiCss.inp)
                                    .clickable { dropdownPisoExpandido = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = nuevoPiso,
                                        style = WiText.body,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = null,
                                        tint = WiCss.tx3
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = dropdownPisoExpandido,
                            onDismissRequest = { dropdownPisoExpandido = false },
                            modifier = Modifier.background(WiCss.wb)
                        ) {
                            opcionesPiso.forEach { piso ->
                                DropdownMenuItem(
                                    text = { Text(piso, style = WiText.body, color = WiCss.tx1) },
                                    onClick = {
                                        nuevoPiso = piso
                                        dropdownPisoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    // Dropdown Flotante: Tipo
                    Box(modifier = Modifier.weight(1f)) {
                        Column {
                            Text(text = "Tipo", style = WiText.small, color = WiCss.tx3)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(WiCss.inp)
                                    .clickable { dropdownTipoExpandido = true }
                                    .padding(horizontal = 12.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = nuevoTipo,
                                        style = WiText.body,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = null,
                                        tint = WiCss.tx3
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = dropdownTipoExpandido,
                            onDismissRequest = { dropdownTipoExpandido = false },
                            modifier = Modifier.background(WiCss.wb)
                        ) {
                            opcionesTipo.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo, style = WiText.body, color = WiCss.tx1) },
                                    onClick = {
                                        nuevoTipo = tipo
                                        dropdownTipoExpandido = false
                                    }
                                )
                            }
                        }
                    }
                }

                WiButton(
                    text = "Guardar Nueva Habitación",
                    onClick = {
                        val precio = nuevoPrecioStr.toDoubleOrNull() ?: 80.0
                        if (nuevoNumero.isNotBlank()) {
                            onEditarHabitacion(
                                ModeloHabitacion(
                                    numero = nuevoNumero.trim(),
                                    precio = precio,
                                    piso = nuevoPiso,
                                    tipo = nuevoTipo,
                                    capacidad = 2,
                                    estado = "disponible"
                                )
                            )
                            nuevoNumero = ""
                            nuevoPrecioStr = ""
                        }
                    },
                    variant = WiButtonVariant.Primary,
                    icon = Icons.Rounded.Add,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 2. Lista Directa del Catálogo Tarifario
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Catálogo Tarifario (${uiState.habitaciones.size})",
                    style = WiText.h4,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.habitaciones.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(WiCss.bg)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay habitaciones registradas. Agrega una arriba.",
                            style = WiText.body,
                            color = WiCss.tx3
                        )
                    }
                } else {
                    uiState.habitaciones.forEach { hab ->
                        TarjetaTarifaInteractiva(
                            habitacion = hab,
                            onEditar = { onEditarHabitacion(hab) },
                            onCambiarPrecioRapido = {
                                habitacionEdicionRapidaPrecio = hab
                                nuevoPrecioInput = String.format("%.2f", hab.precio)
                            },
                            onEliminar = { hab.id?.let { id -> onEliminarHabitacion(id) } }
                        )
                    }
                }
            }
        }
    }

    // Modal de Cambio Rápido de Precio
    if (habitacionEdicionRapidaPrecio != null) {
        Dialog(onDismissRequest = { habitacionEdicionRapidaPrecio = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(WiCss.wb)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Cambiar Precio Habitación ${habitacionEdicionRapidaPrecio!!.numero}",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )

                    WiField(
                        value = nuevoPrecioInput,
                        onValueChange = { nuevoPrecioInput = it },
                        label = "Nuevo Precio Noche (S/)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WiButton(
                            text = "Cancelar",
                            onClick = { habitacionEdicionRapidaPrecio = null },
                            variant = WiButtonVariant.Outline,
                            modifier = Modifier.weight(1f)
                        )

                        WiButton(
                            text = "Guardar Precio",
                            onClick = {
                                val np = nuevoPrecioInput.toDoubleOrNull()
                                if (np != null && np > 0) {
                                    habitacionEdicionRapidaPrecio!!.id?.let { id ->
                                        onActualizarPrecioRapido(id, np)
                                    }
                                }
                                habitacionEdicionRapidaPrecio = null
                            },
                            variant = WiButtonVariant.Primary,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaTarifaInteractiva(
    habitacion: ModeloHabitacion,
    onEditar: () -> Unit,
    onCambiarPrecioRapido: () -> Unit,
    onEliminar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WiCss.bg)
            .border(1.dp, WiCss.brd, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fila Superior: Datos de Cuarto + Precio Destacado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Habitación ${habitacion.numero}",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${habitacion.piso} • ${habitacion.tipo} • Max ${habitacion.capacidad} pers.",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }

                // Badge Clickable de Precio
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.mco.copy(alpha = 0.15f))
                        .clickable(onClick = onCambiarPrecioRapido)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "S/ ${String.format("%.2f", habitacion.precio)}",
                        style = WiText.h4,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Badges de Atributos Tarifarios (desayuno, baño, tv)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (habitacion.conDesayuno) BadgeAtributo("Desayuno Buffet", WiIcons.Coffee)
                if (habitacion.conBano) BadgeAtributo("Baño Privado", WiIcons.Bathtub)
                if (habitacion.conTv) BadgeAtributo("Smart TV", WiIcons.Tv)
            }

            // Botones de Acción Simétricos Idénticos (Editar y Eliminar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Editar Cuarto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.mco.copy(alpha = 0.12f))
                        .clickable(onClick = onEditar),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar Cuarto",
                            tint = WiCss.mco,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Editar Cuarto",
                            style = WiText.body,
                            color = WiCss.mco,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Eliminar Cuarto (Exactamente las mismas dimensiones)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.error.copy(alpha = 0.12f))
                        .clickable(onClick = onEliminar),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar",
                            tint = WiCss.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Eliminar",
                            style = WiText.body,
                            color = WiCss.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeAtributo(texto: String, icono: ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(WiCss.inp)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = WiCss.tx2,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = texto,
                style = WiText.small,
                color = WiCss.tx2,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
