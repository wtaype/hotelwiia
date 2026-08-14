package com.hotelwii.feature.recepcion.tabs

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Refresh
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
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.recepcion.RecepcionUiState

/**
 * 📅 Reservas.kt — Sub-Pestaña 0: Calendario Mensual en 2do Plano + Sync Booking/Expedia + Pull-To-Refresh.
 */
@Composable
fun Reservas(
    uiState: RecepcionUiState,
    onRefrescarReservas: () -> Unit
) {
    var timestampSync by remember { mutableStateOf("Actualizado hace 1 min") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Sync OTAs (Booking, Expedia, Directas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Calendario Mensual & Sync OTAs",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    GoldPill(text = "BOOKING / EXPEDIA")
                }

                Text(
                    text = "Las reservas se sincronizan automáticamente en segundo plano. Estado: $timestampSync",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                WiButton(
                    text = "Sincronizar Reservas Ahora",
                    onClick = {
                        onRefrescarReservas()
                        timestampSync = "Actualizado ahora mismo"
                    },
                    variant = WiButtonVariant.Secondary,
                    icon = Icons.Rounded.Refresh,
                    loading = uiState.isRefreshing,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Listado de Reservas Programadas por Días del Mes
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Próximos Ingresos & Reservas Directas",
                    style = WiText.h4,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.reservas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(WiCss.bg)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay reservas programadas para los próximos días.",
                            style = WiText.body,
                            color = WiCss.tx3
                        )
                    }
                } else {
                    uiState.reservas.forEach { res ->
                        ItemReservaCard(
                            cliente = res.clienteNombre,
                            canal = res.canal.uppercase(),
                            fechas = "${res.fechaInicio} ➔ ${res.fechaFin}",
                            adelanto = res.montoAdelanto
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemReservaCard(
    cliente: String,
    canal: String,
    fechas: String,
    adelanto: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WiCss.bg)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente,
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fechas,
                    style = WiText.small,
                    color = WiCss.tx3
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                GoldPill(text = canal)
                if (adelanto > 0) {
                    Text(
                        text = "Adelanto: S/ ${String.format("%.2f", adelanto)}",
                        style = WiText.small,
                        color = WiCss.success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
