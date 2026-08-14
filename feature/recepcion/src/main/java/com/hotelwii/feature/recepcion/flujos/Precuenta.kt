package com.hotelwii.feature.recepcion.flujos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.recepcion.components.Deslizable
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta

/**
 * 📋 Precuenta.kt — Hoja Deslizable (Bottom Sheet con Drag-Handle `—`) para Desglose de Precuenta antes de Check-Out.
 * Botón único de acción a ancho completo (100%) sin botón redundante de cerrar.
 */
@Composable
fun Precuenta(
    habitacion: ModeloHabitacion,
    venta: ModeloVenta?,
    onCerrar: () -> Unit,
    onIrAPagar: () -> Unit
) {
    val alquiler = venta?.montoAlquiler ?: habitacion.precio
    val consumos = venta?.montoConsumos ?: 0.0
    val adelanto = venta?.montoAdelanto ?: 0.0
    val totalPagar = (alquiler + consumos - adelanto).coerceAtLeast(0.0)

    Deslizable(
        onCerrar = onCerrar,
        titulo = "Precuenta Habitación ${habitacion.numero}",
        subtitulo = "Huésped: ${venta?.clienteNombre ?: "Huésped Registrado"}",
        icono = Icons.Rounded.Info
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WiCss.bg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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

        Spacer(Modifier.height(4.dp))

        // Botón Único a Ancho Completo (100% visible sin truncamiento)
        WiButton(
            text = "Proceder al Pago / Check-Out",
            onClick = onIrAPagar,
            variant = WiButtonVariant.Primary,
            icon = Icons.Rounded.ShoppingCart,
            modifier = Modifier.fillMaxWidth()
        )
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
