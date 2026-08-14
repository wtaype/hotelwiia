package com.hotelwii.feature.recepcion.flujos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.recepcion.components.Deslizable
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 📝 Registro.kt — Hoja Deslizable (Bottom Sheet Nativo) para Creación / Edición Rápida de Cuarto.
 * Botón único full-width sin botón redundante de cancelar.
 */
@Composable
fun Registro(
    habitacion: ModeloHabitacion? = null,
    onCerrar: () -> Unit,
    onGuardar: (ModeloHabitacion) -> Unit
) {
    var numero by remember { mutableStateOf(habitacion?.numero ?: "") }
    var piso by remember { mutableStateOf(habitacion?.piso ?: "Piso 1") }
    var tipo by remember { mutableStateOf(habitacion?.tipo ?: "Matrimonial") }
    var precioStr by remember { mutableStateOf(habitacion?.precio?.let { String.format("%.2f", it) } ?: "80.00") }
    var capacidadStr by remember { mutableStateOf(habitacion?.capacidad?.toString() ?: "2") }
    var conBano by remember { mutableStateOf(habitacion?.conBano ?: true) }
    var conTv by remember { mutableStateOf(habitacion?.conTv ?: true) }
    var estaActivaCheckIn by remember { mutableStateOf(habitacion?.estado?.lowercase() != "limpieza" && habitacion?.estado?.lowercase() != "mantenimiento") }

    var dropdownPisoExpandido by remember { mutableStateOf(false) }
    var dropdownTipoExpandido by remember { mutableStateOf(false) }

    val opcionesPiso = listOf("Piso 1", "Piso 2", "Piso 3", "Piso 4", "Suites Dunas")
    val opcionesTipo = listOf("Simple", "Matrimonial", "Doble", "Triple", "Suite Jacuzzi")

    val precioDouble = precioStr.toDoubleOrNull() ?: 80.0

    Deslizable(
        onCerrar = onCerrar,
        titulo = if (habitacion?.id != null) "Editar Habitación ${habitacion.numero}" else "Registrar Nueva Habitación",
        subtitulo = "Configuración de tarifa, piso, tipo y servicios",
        icono = Icons.Rounded.Home
    ) {
        // Banner de Vista Previa de Tarifa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WiCss.mco.copy(alpha = 0.12f))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TARIFA BASE POR NOCHE",
                        style = WiText.label,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (numero.isNotBlank()) "Cuarto $numero ($tipo)" else "Configuración rápida",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }
                Text(
                    text = "S/ ${String.format("%.2f", precioDouble)}",
                    style = WiText.h3,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Fila 1: Número de Cuarto y Precio
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WiField(
                value = numero,
                onValueChange = { numero = it },
                label = "Número de Cuarto (ej: 101)",
                modifier = Modifier.weight(1f)
            )

            WiField(
                value = precioStr,
                onValueChange = { precioStr = it },
                label = "Precio Noche (S/)",
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: Piso y Tipo con Dropdowns Flotantes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Piso
            Box(modifier = Modifier.weight(1f)) {
                Column {
                    Text(text = "Piso / Nivel", style = WiText.small, color = WiCss.tx3)
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
                                text = piso,
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
                    opcionesPiso.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p, style = WiText.body, color = WiCss.tx1) },
                            onClick = {
                                piso = p
                                dropdownPisoExpandido = false
                            }
                        )
                    }
                }
            }

            // Tipo
            Box(modifier = Modifier.weight(1f)) {
                Column {
                    Text(text = "Tipo de Habitación", style = WiText.small, color = WiCss.tx3)
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
                                text = tipo,
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
                    opcionesTipo.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t, style = WiText.body, color = WiCss.tx1) },
                            onClick = {
                                tipo = t
                                dropdownTipoExpandido = false
                            }
                        )
                    }
                }
            }
        }

        // Capacidad
        WiField(
            value = capacidadStr,
            onValueChange = { capacidadStr = it },
            label = "Capacidad Máxima de Huéspedes",
            modifier = Modifier.fillMaxWidth()
        )

        // Switches de Servicios Clave
        WiSwitch(
            checked = conBano,
            onCheckedChange = { conBano = it },
            label = "Baño Privado Incorporado",
            sublabel = "Indica si la habitación cuenta con baño privado propio."
        )

        WiSwitch(
            checked = conTv,
            onCheckedChange = { conTv = it },
            label = "Smart TV / Streaming",
            sublabel = "Incluye televisor con servicio de cable o internet."
        )

        WiSwitch(
            checked = estaActivaCheckIn,
            onCheckedChange = { estaActivaCheckIn = it },
            label = "Habitación Activa para Check-In",
            sublabel = "Lista para recibir huéspedes en el Rack de Recepción."
        )

        Spacer(Modifier.height(4.dp))

        // Botón Único a Ancho Completo (100% visible sin truncamiento)
        WiButton(
            text = "Guardar Habitación",
            onClick = {
                val estadoFinal = if (habitacion?.estado == "ocupada") "ocupada"
                else if (estaActivaCheckIn) "disponible" else "limpieza"

                val h = ModeloHabitacion(
                    id = habitacion?.id,
                    numero = numero.trim(),
                    piso = piso.trim(),
                    tipo = tipo.trim(),
                    precio = precioDouble,
                    capacidad = capacidadStr.toIntOrNull() ?: 2,
                    estado = estadoFinal,
                    conDesayuno = false,
                    conBano = conBano,
                    conTv = conTv,
                    amenidades = if (conBano && conTv) "Baño Privado, Smart TV" else if (conBano) "Baño Privado" else "Smart TV",
                    observaciones = ""
                )
                onGuardar(h)
            },
            variant = WiButtonVariant.Primary,
            icon = Icons.Rounded.Check,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
