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
import androidx.compose.material.icons.rounded.ShoppingCart
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
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.feature.recepcion.components.Fijo
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta

/**
 * 💳 PagoPos.kt — Flujo Fijo Dedicado para Cobro POS Instantáneo, Cálculo de Vuelto & Emisión SUNAT.
 */
@Composable
fun PagoPos(
    habitacion: ModeloHabitacion,
    venta: ModeloVenta?,
    onCerrar: () -> Unit,
    onConfirmarPagoCheckOut: (metodoPago: String, tipoComprobante: String, montoFinal: Double) -> Unit
) {
    val totalOriginal = (venta?.montoTotal ?: habitacion.precio) - (venta?.montoAdelanto ?: 0.0)
    var metodoPago by remember { mutableStateOf("efectivo") }
    var tipoComprobante by remember { mutableStateOf("nota_venta") }
    var montoRecibidoStr by remember { mutableStateOf(String.format("%.2f", totalOriginal.coerceAtLeast(0.0))) }

    val montoRecibido = montoRecibidoStr.toDoubleOrNull() ?: 0.0
    val vuelto = (montoRecibido - totalOriginal).coerceAtLeast(0.0)

    Fijo(
        onCerrar = onCerrar,
        onVolver = onCerrar,
        titulo = "Cobro POS & Check-Out Hab. ${habitacion.numero}",
        subtitulo = "Selección de medio de pago y emisión fiscal SUNAT",
        icono = Icons.Rounded.ShoppingCart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WiCss.mco.copy(alpha = 0.12f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MONTO FINAL A COBRAR",
                    style = WiText.label,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S/ ${String.format("%.2f", totalOriginal.coerceAtLeast(0.0))}",
                    style = WiText.h2,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        WiSelect(
            options = listOf("Efectivo", "Yape", "Plin", "Tarjeta POS", "Mixto / Otros"),
            selectedOption = when (metodoPago) {
                "efectivo" -> "Efectivo"
                "yape" -> "Yape"
                "plin" -> "Plin"
                "tarjeta" -> "Tarjeta POS"
                else -> "Efectivo"
            },
            onOptionSelected = { op ->
                metodoPago = when (op) {
                    "Efectivo" -> "efectivo"
                    "Yape" -> "yape"
                    "Plin" -> "plin"
                    "Tarjeta POS" -> "tarjeta"
                    else -> "efectivo"
                }
            },
            label = "Método de Pago"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WiField(
                value = montoRecibidoStr,
                onValueChange = { montoRecibidoStr = it },
                label = "Monto Recibido (S/)",
                modifier = Modifier.weight(1f)
            )

            WiField(
                value = "S/ ${String.format("%.2f", vuelto)}",
                onValueChange = {},
                label = "Vuelto Entregado (S/)",
                modifier = Modifier.weight(1f)
            )
        }

        WiSelect(
            options = listOf("Nota de Venta (Interno)", "Boleta Electrónica (B001)", "Factura Electrónica (F001)"),
            selectedOption = when (tipoComprobante) {
                "nota_venta" -> "Nota de Venta (Interno)"
                "boleta" -> "Boleta Electrónica (B001)"
                "factura" -> "Factura Electrónica (F001)"
                else -> "Nota de Venta (Interno)"
            },
            onOptionSelected = { op ->
                tipoComprobante = when (op) {
                    "Nota de Venta (Interno)" -> "nota_venta"
                    "Boleta Electrónica (B001)" -> "boleta"
                    "Factura Electrónica (F001)" -> "factura"
                    else -> "nota_venta"
                }
            },
            label = "Emisión de Comprobante"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WiButton(
                text = "Cancelar",
                onClick = onCerrar,
                variant = WiButtonVariant.Cancel,
                modifier = Modifier.weight(1f)
            )

            WiButton(
                text = "Cobrar y Finalizar Check-Out",
                onClick = {
                    onConfirmarPagoCheckOut(metodoPago, tipoComprobante, totalOriginal.coerceAtLeast(0.0))
                },
                variant = WiButtonVariant.Primary,
                icon = Icons.Rounded.Check,
                modifier = Modifier.weight(1.4f)
            )
        }
    }
}
