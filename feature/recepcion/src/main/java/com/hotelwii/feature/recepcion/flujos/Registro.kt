package com.hotelwii.feature.recepcion.flujos

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.recepcion.components.Fijo
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 📝 Registro.kt — Pantalla Completa Dedicada (Fijo.kt) con Selector Smart de Capacidad Dinámica (x) y Presets Frecuentes.
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
    var capacidad by remember { mutableIntStateOf(habitacion?.capacidad ?: 2) }
    var conDesayuno by remember { mutableStateOf(habitacion?.conDesayuno ?: false) }
    var conBano by remember { mutableStateOf(habitacion?.conBano ?: false) } // Por defecto en OFF
    var amenidades by remember { mutableStateOf(habitacion?.amenidades ?: "Wi-Fi, Agua Caliente") }
    var observaciones by remember { mutableStateOf(habitacion?.observaciones ?: "") }
    var estaActivaCheckIn by remember { mutableStateOf(habitacion?.estado?.lowercase() != "limpieza" && habitacion?.estado?.lowercase() != "mantenimiento") }

    var dropdownPisoExpandido by remember { mutableStateOf(false) }
    var dropdownTipoExpandido by remember { mutableStateOf(false) }

    val opcionesPiso = listOf("Piso 1", "Piso 2", "Piso 3", "Piso 4", "Suites Dunas", "Bungalows")
    val opcionesTipo = listOf("Simple", "Matrimonial", "Doble", "Triple", "Suite Jacuzzi", "Familiar")
    val preciosPreset = listOf(80.0, 100.0, 120.0, 180.0, 240.0)

    val precioDouble = precioStr.toDoubleOrNull() ?: 80.0

    Fijo(
        onCerrar = onCerrar,
        titulo = if (habitacion?.id != null) "Editar Habitación ${habitacion.numero}" else "Registrar Habitación",
        subtitulo = "Ajuste tarifario y servicios",
        icono = Icons.Rounded.Home
    ) {
        // 💎 BANNER DE VISTA PREVIA TARIFARIA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WiCss.mco.copy(alpha = 0.12f))
                .border(1.dp, WiCss.mco.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        text = if (numero.isNotBlank()) "Habitación $numero ($tipo)" else "Configuración tarifaria",
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

        // 📐 BLOQUE 1: N° DE CUARTO + PRECIO + PRESETS FRECUENTES (PEGADOS ARRIBA)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WiField(
                    value = numero,
                    onValueChange = { numero = it },
                    label = "N° de Cuarto",
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = precioStr,
                    onValueChange = { precioStr = it },
                    label = "Precio Noche (S/)",
                    modifier = Modifier.weight(1f)
                )
            }

            // ⚡ CHIPS RÁPIDOS DE TARIFAS FRECUENTES (80, 100, 120, 180, 240)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                preciosPreset.forEach { p ->
                    val seleccionado = (precioDouble == p)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (seleccionado) WiCss.mco else WiCss.inp)
                            .clickable { precioStr = String.format("%.2f", p) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S/${p.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (seleccionado) WiCss.wb else WiCss.tx2
                        )
                    }
                }
            }
        }

        // 📐 BLOQUE 2: PISO Y TIPO (DROPDOWNS ALINEADOS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
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

        // 👥 BLOQUE 3: SELECTOR SMART DE CAPACIDAD DE PERSONAS (STEPPER + PILLS 1..4 + X DINÁMICA)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capacidad de Personas",
                    style = WiText.small,
                    color = WiCss.tx3
                )
                Text(
                    text = "$capacidad ${if (capacidad == 1) "huésped" else "huéspedes"}",
                    style = WiText.small,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Menos [ - ]
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.inp)
                        .clickable { if (capacidad > 1) capacidad-- },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "—",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiCss.tx1
                    )
                }

                // Pills de Selección Directa (1, 2, 3, 4)
                listOf(1, 2, 3, 4).forEach { num ->
                    val esActivo = (capacidad == num)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (esActivo) WiCss.mco else WiCss.inp)
                            .clickable { capacidad = num },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$num",
                            style = WiText.body,
                            fontWeight = FontWeight.Bold,
                            color = if (esActivo) WiCss.wb else WiCss.tx1
                        )
                    }
                }

                // 5ta Pill Dinámica (x): Si capacidad >= 5 se actualiza al número actual (5, 6, 7, 8...) y se activa
                val esActivoQuinto = (capacidad >= 5)
                val textoQuinto = if (capacidad >= 5) "$capacidad" else "5+"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (esActivoQuinto) WiCss.mco else WiCss.inp)
                        .clickable { if (capacidad < 5) capacidad = 5 },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textoQuinto,
                        style = WiText.body,
                        fontWeight = FontWeight.Bold,
                        color = if (esActivoQuinto) WiCss.wb else WiCss.tx1
                    )
                }

                // Botón Más [ + ]
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.inp)
                        .clickable { if (capacidad < 12) capacidad++ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Aumentar capacidad",
                        tint = WiCss.tx1,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ⚙️ SWITCHES OPERATIVOS (ORDEN: 1° DESAYUNO, 2° BAÑO PRIVADO, 3° CHECK-IN)
        WiSwitch(
            checked = conDesayuno,
            onCheckedChange = { conDesayuno = it },
            label = "Desayuno Incluido",
            sublabel = "Incluye servicio de desayuno diario."
        )

        WiSwitch(
            checked = conBano,
            onCheckedChange = { conBano = it },
            label = "Baño Privado Incorporado",
            sublabel = "Habitación con servicios higiénicos propios."
        )

        WiSwitch(
            checked = estaActivaCheckIn,
            onCheckedChange = { estaActivaCheckIn = it },
            label = "Listo para Check-In",
            sublabel = "Disponible para asignar a huéspedes en recepción."
        )

        // Amenidades y Observaciones
        WiField(
            value = amenidades,
            onValueChange = { amenidades = it },
            label = "Amenidades y Servicios",
            modifier = Modifier.fillMaxWidth()
        )

        WiField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = "Notas Internas de Recepción",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // 🚀 BOTÓN PRINCIPAL ÚNICO
        WiButton(
            text = if (habitacion?.id != null) "Guardar Cambios" else "Crear Habitación",
            onClick = {
                val estadoFinal = if (habitacion?.estado == "ocupada") "ocupada"
                else if (estaActivaCheckIn) "disponible" else "limpieza"

                val h = ModeloHabitacion(
                    id = habitacion?.id,
                    numero = numero.trim(),
                    piso = piso.trim(),
                    tipo = tipo.trim(),
                    precio = precioDouble,
                    capacidad = capacidad,
                    estado = estadoFinal,
                    conDesayuno = conDesayuno,
                    conBano = conBano,
                    conTv = true,
                    amenidades = amenidades.trim(),
                    observaciones = observaciones.trim()
                )
                onGuardar(h)
            },
            variant = WiButtonVariant.Primary,
            icon = WiIcons.Check,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
