package com.hotelwii.feature.imprimir.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora
import com.hotelwii.feature.imprimir.servicios.TipoConexion

/**
 * Estados — Tarjeta de estado en vivo, diagnóstico de latencia y calibración para ticketera 3nStar.
 */
@Composable
fun Estados(
    config: ModeloConfigImpresora,
    isProbando: Boolean,
    isImprimiendo: Boolean,
    ultimoMensaje: String?,
    esError: Boolean,
    onComprobarConexion: () -> Unit,
    onImprimirPrueba: () -> Unit,
    onEditarConfiguracion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera con Estado y Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WiCss.mco.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = WiIcons.Print,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Impresora 3nStar",
                        style = WiText.h3,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (config.tipoConexion == TipoConexion.RED_TCP) {
                            "Red Socket: ${config.ip}:${config.puerto}"
                        } else {
                            "Bluetooth: ${config.macBluetooth}"
                        },
                        style = WiText.small,
                        color = WiCss.tx2
                    )
                }
            }

            // Badge de Estado
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (config.estaConfigurada) WiCss.success.copy(alpha = 0.12f)
                        else WiCss.offline.copy(alpha = 0.5f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (config.estaConfigurada) WiCss.success else WiCss.tx3)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if (config.estaConfigurada) "VINCULADA" else "SIN CONFIGURAR",
                        style = WiText.tiny,
                        color = if (config.estaConfigurada) WiCss.success else WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Especificaciones Rápidas de Hardware
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WiCss.inp)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "ANCHO DE PAPEL",
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${config.anchoPapel.columnas} Columnas (${config.anchoPapel.milimetros} mm)",
                        style = WiText.small,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WiCss.inp)
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "AUTO-CORTE",
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (config.cortarPapel) "Activado" else "Desactivado",
                        style = WiText.small,
                        color = if (config.cortarPapel) WiCss.success else WiCss.tx2,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Mensaje de Diagnóstico / Logs en Tiempo Real
        if (!ultimoMensaje.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (esError) WiCss.error.copy(alpha = 0.10f)
                        else WiCss.success.copy(alpha = 0.10f)
                    )
                    .border(
                        1.dp,
                        if (esError) WiCss.error.copy(alpha = 0.3f) else WiCss.success.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (esError) Icons.Rounded.Warning else Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = if (esError) WiCss.error else WiCss.success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = ultimoMensaje,
                        style = WiText.small,
                        color = if (esError) WiCss.error else WiCss.success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Acciones Principales
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WiButton(
                text = if (isImprimiendo) "Imprimiendo en 3nStar..." else "Imprimir Ticket de Prueba",
                onClick = onImprimirPrueba,
                variant = WiButtonVariant.Primary,
                icon = WiIcons.Print,
                loading = isImprimiendo,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WiButton(
                    text = if (isProbando) "Probando..." else "Comprobar Ping",
                    onClick = onComprobarConexion,
                    variant = WiButtonVariant.Secondary,
                    icon = Icons.Rounded.Refresh,
                    loading = isProbando,
                    modifier = Modifier.weight(1f)
                )

                WiButton(
                    text = "Editar IP / Ajustes",
                    onClick = onEditarConfiguracion,
                    variant = WiButtonVariant.Secondary,
                    icon = Icons.Rounded.Settings,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
