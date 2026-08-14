package com.hotelwii.feature.recepcion.flujos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Home
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
import com.hotelwii.feature.recepcion.components.Deslizable
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * 📝 Registro.kt — Hoja Deslizable (Bottom Sheet con Drag-Handle `—`) para Creación / Edición de Cuarto.
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
    var conDesayuno by remember { mutableStateOf(habitacion?.conDesayuno ?: false) }
    var conBano by remember { mutableStateOf(habitacion?.conBano ?: true) }
    var conTv by remember { mutableStateOf(habitacion?.conTv ?: true) }
    var amenidades by remember { mutableStateOf(habitacion?.amenidades ?: "Wi-Fi, Agua Caliente, TV") }
    var observaciones by remember { mutableStateOf(habitacion?.observaciones ?: "") }

    val precioDouble = precioStr.toDoubleOrNull() ?: 80.0

    Deslizable(
        onCerrar = onCerrar,
        titulo = if (habitacion?.id != null) "Editar Habitación ${habitacion.numero}" else "Registrar Nueva Habitación",
        subtitulo = "Configuración de tarifas, piso y amenidades",
        icono = Icons.Rounded.Home
    ) {
        // Vista previa de tarifa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(WiCss.mco.copy(alpha = 0.12f))
                .padding(14.dp),
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

        WiSwitch(
            checked = conDesayuno,
            onCheckedChange = { conDesayuno = it },
            label = "Incluye Desayuno Buffet",
            sublabel = "Habilita servicio de desayuno en la tarifa base."
        )

        WiSwitch(
            checked = conBano,
            onCheckedChange = { conBano = it },
            label = "Baño Privado Incorporado",
            sublabel = "Indica si la habitación cuenta con baño propio."
        )

        WiSwitch(
            checked = conTv,
            onCheckedChange = { conTv = it },
            label = "Smart TV / Cable",
            sublabel = "Incluye televisor con servicio de streaming o cable."
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
