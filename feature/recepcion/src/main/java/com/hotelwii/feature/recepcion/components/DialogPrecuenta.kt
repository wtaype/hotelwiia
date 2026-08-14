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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta

/**
 * 📋 DialogPrecuenta.kt — Modal de Precuenta antes de facturar (Hospedaje + Consumos de Minibar/Servicios).
 */
@Composable
fun DialogPrecuenta(
    habitacion: ModeloHabitacion,
    venta: ModeloVenta?,
    onDismiss: () -> Unit,
    onIrAPagar: () -> Unit
) {
    val alquiler = venta?.montoAlquiler ?: habitacion.precio
    val consumos = venta?.montoConsumos ?: 0.0
    val adelanto = venta?.montoAdelanto ?: 0.0
    val totalPagar = (alquiler + consumos - adelanto).coerceAtLeast(0.0)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(WiCss.wb)
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Precuenta Habitación ${habitacion.numero}",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Huésped: ${venta?.clienteNombre ?: "Huésped Registrado"}",
                    style = WiText.body,
                    color = WiCss.tx2,
                    fontWeight = FontWeight.Medium
                )

                // Desglose de Precuenta
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WiCss.bg)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItemFilaPrecuenta("Alquiler de Habitación (${habitacion.tipo})", "S/ ${String.format("%.2f", alquiler)}")
                    if (consumos > 0) {
                        ItemFilaPrecuenta("Consumos Minibar / Extras", "S/ ${String.format("%.2f", consumos)}")
                    }
                    if (adelanto > 0) {
                        ItemFilaPrecuenta("Adelanto Abonado", "- S/ ${String.format("%.2f", adelanto)}")
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL A PAGAR",
                            style = WiText.body,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "S/ ${String.format("%.2f", totalPagar)}",
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
                    WiButton(
                        text = "Cerrar",
                        onClick = onDismiss,
                        variant = WiButtonVariant.Outline,
                        modifier = Modifier.weight(1f)
                    )

                    WiButton(
                        text = "Proceder al Pago / Check-Out",
                        onClick = onIrAPagar,
                        variant = WiButtonVariant.Primary,
                        modifier = Modifier.weight(1.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemFilaPrecuenta(concepto: String, montoStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = concepto,
            style = WiText.small,
            color = WiCss.tx2
        )
        Text(
            text = montoStr,
            style = WiText.small,
            color = WiCss.tx1,
            fontWeight = FontWeight.SemiBold
        )
    }
}
