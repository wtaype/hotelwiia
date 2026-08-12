package com.hotelwii.feature.cuenta.tabs

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
import androidx.compose.material.icons.rounded.Settings
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
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiSwitch

/**
 * ⚙️ Ajustes.kt — Pestaña 4: Preferencias de Recepción & Gestión de Caché wiStore.
 */
@Composable
fun Ajustes() {
    var notificacionesActivas by remember { mutableStateOf(true) }
    var impresorPosActivo by remember { mutableStateOf(true) }
    var mensajeCacheLimpiada by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Preferencias del Hotel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Preferencias de Recepción",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                WiSwitch(
                    checked = notificacionesActivas,
                    onCheckedChange = { notificacionesActivas = it },
                    label = "Notificaciones de Reservas",
                    sublabel = "Recibe alertas en tiempo real de nuevas reservas e ingresos."
                )

                WiSwitch(
                    checked = impresorPosActivo,
                    onCheckedChange = { impresorPosActivo = it },
                    label = "Impresión de Tickets POS ESC/POS",
                    sublabel = "Genera comprobantes térmicos de check-in automáticamente."
                )
            }
        }

        // 2. Almacenamiento & Caché Local wiStore
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = WiIcons.Refresh,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Almacenamiento Local wiStore",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado de la Caché Local",
                        style = WiText.small,
                        color = WiCss.tx2
                    )
                    Text(
                        text = "Saludable (0.4 MB)",
                        style = WiText.small,
                        color = WiCss.success,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (mensajeCacheLimpiada != null) {
                    Text(
                        text = mensajeCacheLimpiada!!,
                        style = WiText.small,
                        color = WiCss.success
                    )
                }

                WiButton(
                    text = "Limpiar Caché Residual",
                    onClick = {
                        mensajeCacheLimpiada = "Caché residual optimizada correctamente."
                    },
                    icon = WiIcons.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
