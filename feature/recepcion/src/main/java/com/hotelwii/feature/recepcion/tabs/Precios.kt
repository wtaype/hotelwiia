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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🏷️ Precios.kt — Sub-Pestaña 2: Catálogo Tarifario con Tarjeta de Registro Rápido que abre Deslizable.kt.
 */
@Composable
fun Precios(
    uiState: RecepcionUiState,
    onNuevaHabitacion: () -> Unit,
    onEditarHabitacion: (ModeloHabitacion) -> Unit,
    onActualizarPrecioRapido: (String, Double) -> Unit,
    onEliminarHabitacion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta Rápida Superior para Registrar Nueva Habitación (Abre Deslizable.kt)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.mco.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .clickable(onClick = onNuevaHabitacion)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Registrar Nueva Habitación",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Toca aquí para configurar número, tarifa y piso",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        }

        // 2. Catálogo Tarifario: Tarjetas Clickables con Íconos de Acción Compactos
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
                            text = "No hay habitaciones registradas. Toca arriba para agregar una.",
                            style = WiText.body,
                            color = WiCss.tx3
                        )
                    }
                } else {
                    uiState.habitaciones.forEach { hab ->
                        TarjetaTarifaInteractiva(
                            habitacion = hab,
                            onEditar = { onEditarHabitacion(hab) },
                            onEliminar = { hab.id?.let { id -> onEliminarHabitacion(id) } }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🏷️ TarjetaTarifaInteractiva: Clickable completa para abrir Deslizable.kt con íconos compactos de acción.
 */
@Composable
private fun TarjetaTarifaInteractiva(
    habitacion: ModeloHabitacion,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(WiCss.bg)
            .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
            .clickable(onClick = onEditar)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fila Superior: Datos de Cuarto + Precio Destacado + Íconos Compactos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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

                // Grupo Derecho: Badge de Precio + Íconos Compactos de Acción
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge de Precio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.mco.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "S/ ${String.format("%.2f", habitacion.precio)}",
                            style = WiText.h4,
                            color = WiCss.mco,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Ícono Compacto: Editar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f))
                            .clickable(onClick = onEditar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar Cuarto",
                            tint = WiCss.mco,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Ícono Compacto: Eliminar
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(WiCss.error.copy(alpha = 0.12f))
                            .clickable(onClick = onEliminar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar",
                            tint = WiCss.error,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Badges de Atributos Tarifarios (baño privado, smart tv)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (habitacion.conBano) BadgeAtributo("Baño Privado", WiIcons.Bathtub)
                if (habitacion.conTv) BadgeAtributo("Smart TV", WiIcons.Tv)
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
