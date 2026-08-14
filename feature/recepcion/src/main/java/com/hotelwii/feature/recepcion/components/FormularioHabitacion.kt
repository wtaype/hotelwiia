package com.hotelwii.feature.recepcion.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
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
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 📝 FormularioHabitacion.kt — Formulario Completo de Registro y Edición de Habitación / Tarifas con Vista Previa.
 */
@Composable
fun FormularioHabitacion(
    habitacion: ModeloHabitacion? = null,
    onGuardar: (ModeloHabitacion) -> Unit,
    modifier: Modifier = Modifier
) {
    var numero by remember { mutableStateOf(habitacion?.numero ?: "") }
    var piso by remember { mutableStateOf(habitacion?.piso ?: "Piso 1") }
    var tipo by remember { mutableStateOf(habitacion?.tipo ?: "Matrimonial") }
    var precioStr by remember { mutableStateOf(habitacion?.precio?.let { String.format("%.2f", it) } ?: "80.00") }
    var capacidadStr by remember { mutableStateOf(habitacion?.capacidad?.toString() ?: "2") }
    var conDesayuno by remember { mutableStateOf(habitacion?.conDesayuno ?: false) }
    var conBano by remember { mutableStateOf(habitacion?.conBano ?: true) }
    var conTv by remember { mutableStateOf(habitacion?.conTv ?: true) }
    var amenidades by remember { mutableStateOf(habitacion?.amenidades ?: "Wi-Fi, Agua Caliente, TV") }
    var observaciones by remember { mutableStateOf(habitacion?.observaciones ?: "") }

    val precioDouble = precioStr.toDoubleOrNull() ?: 80.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(WiCss.wb)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = null,
                    tint = WiCss.mco,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (habitacion?.id != null) "Editar Habitación ${habitacion.numero}" else "Registrar Nueva Habitación",
                    style = WiText.h4,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
            }

            // Preview del Precio Tarifa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WiCss.mco.copy(alpha = 0.12f))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARIFA BASE POR NOCHE",
                        style = WiText.label,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", precioDouble)}",
                        style = WiText.h3,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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

            // Selectores de Piso y Tipo
            WiSelect(
                options = listOf("Piso 1", "Piso 2", "Piso 3", "Piso 4", "Suites Dunas"),
                selectedOption = piso,
                onOptionSelected = { piso = it },
                label = "Piso / Ubicación"
            )

            WiSelect(
                options = listOf("Simple", "Matrimonial", "Doble", "Triple", "Suite Jacuzzi"),
                selectedOption = tipo,
                onOptionSelected = { tipo = it },
                label = "Tipo de Habitación"
            )

            WiField(
                value = capacidadStr,
                onValueChange = { capacidadStr = it },
                label = "Capacidad Máxima (personas)",
                modifier = Modifier.fillMaxWidth()
            )

            // Switches de Atributos Tarifarios
            WiSwitch(
                checked = conDesayuno,
                onCheckedChange = { conDesayuno = it },
                label = "Incluye Desayuno Buffet ☕",
                sublabel = "Habilita servicio de desayuno en la tarifa base."
            )

            WiSwitch(
                checked = conBano,
                onCheckedChange = { conBano = it },
                label = "Baño Privado Incorporado 🚿",
                sublabel = "Indica si la habitación cuenta con baño propio."
            )

            WiSwitch(
                checked = conTv,
                onCheckedChange = { conTv = it },
                label = "TV Cable / Smart TV 📺",
                sublabel = "Incluye televisor con servicio de cable/streaming."
            )

            WiField(
                value = amenidades,
                onValueChange = { amenidades = it },
                label = "Amenidades (ej: Wi-Fi, Aire Acondicionado)",
                modifier = Modifier.fillMaxWidth()
            )

            WiField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = "Notas internas / Mantenimiento",
                modifier = Modifier.fillMaxWidth()
            )

            WiButton(
                text = "Guardar Habitación y Tarifa",
                onClick = {
                    val h = ModeloHabitacion(
                        id = habitacion?.id,
                        numero = numero.trim(),
                        piso = piso.trim(),
                        tipo = tipo.trim(),
                        precio = precioDouble,
                        capacidad = capacidadStr.toIntOrNull() ?: 2,
                        estado = habitacion?.estado ?: "disponible",
                        conDesayuno = conDesayuno,
                        conBano = conBano,
                        conTv = conTv,
                        amenidades = amenidades.trim(),
                        observaciones = observaciones.trim()
                    )
                    onGuardar(h)
                },
                icon = Icons.Rounded.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
