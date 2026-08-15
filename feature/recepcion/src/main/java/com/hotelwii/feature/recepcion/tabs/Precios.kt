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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 🏷️ Precios.kt — Catálogo Tarifario Inteligente con KPIs, Agrupación por Pisos y Ajuste Rápido de Precios (Local-First 0ms).
 */
@Composable
fun Precios(
    uiState: RecepcionUiState,
    onNuevaHabitacion: () -> Unit,
    onEditarHabitacion: (ModeloHabitacion) -> Unit,
    onActualizarPrecioRapido: (String, Double) -> Unit,
    onEliminarHabitacion: (String) -> Unit
) {
    val totalHabs = uiState.habitaciones.size
    val habsDisponibles = uiState.habitaciones.count { it.estado.equals("disponible", ignoreCase = true) }
    val tarifaPromedio = if (totalHabs > 0) uiState.habitaciones.map { it.precio }.average() else 0.0

    val habitacionesPorPiso = uiState.habitaciones.groupBy { it.piso.ifBlank { "Piso 1" } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 📊 1. KPIs SUPERIORES DE GESTIÓN TARIFARIA (3 Cards 0ms)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiCard(
                titulo = "HABITACIONES",
                valor = "$totalHabs cuartos",
                subtitulo = "Total registradas",
                color = WiCss.mco,
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                titulo = "TARIFA PROM.",
                valor = "S/ ${String.format("%.0f", tarifaPromedio)}",
                subtitulo = "Por noche",
                color = WiCss.success,
                modifier = Modifier.weight(1f)
            )

            KpiCard(
                titulo = "DISPONIBLES",
                valor = "$habsDisponibles libres",
                subtitulo = "Para Check-in",
                color = Color(0xFF22D3EE),
                modifier = Modifier.weight(1f)
            )
        }

        // ➕ 2. TARJETA ACCIÓN: REGISTRAR NUEVA HABITACIÓN (Abre Deslizable.kt)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.mco.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .clickable(onClick = onNuevaHabitacion)
                .padding(16.dp)
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(24.dp)
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
                            text = "Configura número, aforo, amenidades y tarifa base",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        }

        // 🏢 3. LISTADO TARIFARIO AGRUPADO POR PISOS O ZONAS
        if (uiState.habitaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiCss.wb)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Aún no has registrado habitaciones en este hotel.",
                        style = WiText.body,
                        color = WiCss.tx2,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Toca el botón superior para agregar el primer cuarto.",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }
            }
        } else {
            habitacionesPorPiso.forEach { (piso, habs) ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Header del Piso
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = piso.uppercase(),
                            style = WiText.label,
                            color = WiCss.tx3,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${habs.size} habitaciones",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }

                    // Habitaciones en este piso
                    habs.forEach { hab ->
                        TarjetaTarifaSmart(
                            habitacion = hab,
                            onEditar = { onEditarHabitacion(hab) },
                            onAjusteRapidoPrecio = { delta ->
                                val nuevo = (hab.precio + delta).coerceAtLeast(10.0)
                                val idOCodigo = if (!hab.id.isNullOrBlank()) hab.id else hab.numero
                                onActualizarPrecioRapido(idOCodigo, nuevo)
                            },
                            onEliminar = {
                                val idOCodigo = if (!hab.id.isNullOrBlank()) hab.id else hab.numero
                                onEliminarHabitacion(idOCodigo)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🏷️ TarjetaTarifaSmart: Ficha de habitación interactiva con ajuste rápido de precios (+/- S/ 10).
 */
@Composable
private fun TarjetaTarifaSmart(
    habitacion: ModeloHabitacion,
    onEditar: () -> Unit,
    onAjusteRapidoPrecio: (delta: Double) -> Unit,
    onEliminar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fila Superior: Número de Cuarto + Precio + Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onEditar)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habitacion.numero,
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        BadgeEstadoMini(habitacion.estado)
                    }
                    Text(
                        text = "${habitacion.tipo} • Capacidad: ${habitacion.capacidad} pers.",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }

                // Ajuste Rápido de Precio In-Place
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón -10
                    BotonAjuste(texto = "-10", onClick = { onAjusteRapidoPrecio(-10.0) })

                    // Badge Tarifa
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WiCss.mco.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "S/ ${String.format("%.0f", habitacion.precio)}",
                            style = WiText.body,
                            color = WiCss.mco,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón +10
                    BotonAjuste(texto = "+10", onClick = { onAjusteRapidoPrecio(10.0) })

                    Spacer(Modifier.width(2.dp))

                    // Botón Editar
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f))
                            .clickable(onClick = onEditar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar",
                            tint = WiCss.mco,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Botón Eliminar
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiCss.error.copy(alpha = 0.12f))
                            .clickable(onClick = onEliminar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar",
                            tint = WiCss.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Badges de Atributos Tarifarios
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (habitacion.conBano) BadgeAtributo("Baño Privado", WiIcons.Bathtub)
                if (habitacion.conDesayuno) BadgeAtributo("Desayuno", WiIcons.Coffee)
            }
        }
    }
}

@Composable
private fun KpiCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WiCss.wb)
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = titulo,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = valor,
                style = WiText.body,
                fontWeight = FontWeight.Bold,
                color = WiCss.tx1
            )
            Text(
                text = subtitulo,
                fontSize = 9.sp,
                color = WiCss.tx3
            )
        }
    }
}

@Composable
private fun BotonAjuste(texto: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WiCss.inp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 10.sp,
            color = WiCss.tx2,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BadgeEstadoMini(estado: String) {
    val (colorBg, colorTxt, label) = when (estado.lowercase()) {
        "disponible" -> Triple(WiCss.success.copy(alpha = 0.15f), WiCss.success, "Disponible")
        "ocupada" -> Triple(WiCss.error.copy(alpha = 0.15f), WiCss.error, "Ocupada")
        "limpieza" -> Triple(Color(0xFFEAB308).copy(alpha = 0.15f), Color(0xFFEAB308), "Limpieza")
        else -> Triple(WiCss.tx3.copy(alpha = 0.15f), WiCss.tx3, estado.replaceFirstChar { it.uppercase() })
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colorBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = colorTxt,
            fontWeight = FontWeight.Bold
        )
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
