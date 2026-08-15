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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
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
 * 📊 Estados.kt — Centro de Control, Estado en Vivo y Despacho Inteligente (Local / Nube).
 */
@Composable
fun Estados(
    config: ModeloConfigImpresora,
    isConectadoLocal: Boolean,
    isConectadoNube: Boolean,
    isProbando: Boolean,
    isImprimiendo: Boolean,
    isEscaneando: Boolean = false,
    ultimoMensaje: String?,
    esError: Boolean,
    onEscanearRed: () -> Unit = {},
    onComprobarConexion: () -> Unit,
    onImprimirPrueba: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera Limpia con Identificador de Hardware
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WiCss.mco.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = WiIcons.Print,
                    contentDescription = null,
                    tint = WiCss.mco,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Impresora Térmica 3nStar",
                    style = WiText.h3,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Emisión de tickets de recepción, boletas y vouchers",
                    style = WiText.small,
                    color = WiCss.tx2
                )
            }
        }

        // ==========================================
        // 🌐 FILA 1: ESTADOS EN 2 COLUMNAS (LOCAL vs NUBE)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tarjeta 1: Estado Local Wi-Fi
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isConectadoLocal) WiCss.success.copy(alpha = 0.10f)
                        else WiCss.error.copy(alpha = 0.08f)
                    )
                    .border(
                        1.dp,
                        if (isConectadoLocal) WiCss.success.copy(alpha = 0.3f)
                        else WiCss.error.copy(alpha = 0.25f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WI-FI LOCAL",
                            style = WiText.tiny,
                            color = if (isConectadoLocal) WiCss.success else WiCss.error,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isConectadoLocal) Icons.Rounded.CheckCircle else Icons.Rounded.Close,
                            contentDescription = null,
                            tint = if (isConectadoLocal) WiCss.success else WiCss.error,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (isConectadoLocal) "Conectado (15ms)" else "Desconectado",
                        style = WiText.small,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${config.ip}:${config.puerto}",
                        style = WiText.tiny,
                        color = WiCss.tx2
                    )
                }
            }

            // Tarjeta 2: Estado Nube Supabase Realtime
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isConectadoNube) WiCss.hv.copy(alpha = 0.10f)
                        else WiCss.inp
                    )
                    .border(
                        1.dp,
                        if (isConectadoNube) WiCss.hv.copy(alpha = 0.3f)
                        else WiCss.tx3.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NUBE SUPABASE",
                            style = WiText.tiny,
                            color = if (isConectadoNube) WiCss.hv else WiCss.tx3,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isConectadoNube) Icons.Rounded.CheckCircle else Icons.Rounded.Close,
                            contentDescription = null,
                            tint = if (isConectadoNube) WiCss.hv else WiCss.tx3,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (isConectadoNube) "Realtime Activo" else "Inactivo",
                        style = WiText.small,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Receptor de Hotel",
                        style = WiText.tiny,
                        color = WiCss.tx2
                    )
                }
            }
        }

        // ==========================================
        // ⚙️ FILA 2: DATOS DE HARDWARE EN 2 COLUMNAS
        // ==========================================
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "FORMATO DE PAPEL",
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${config.anchoPapel.milimetros} mm (${config.anchoPapel.columnas} cols)",
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "AUTO-CORTE",
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (config.cortarPapel) "Activado (ESC/POS)" else "Desactivado",
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

        // Botón Principal Inteligente
        val textoBotonPrincipal = when {
            isImprimiendo -> "Enviando impresión..."
            isConectadoLocal -> "Imprimir Ticket de Prueba (Local 15ms)"
            isConectadoNube -> "Enviar Impresión a Recepción (Nube)"
            else -> "Imprimir Ticket de Prueba"
        }

        WiButton(
            text = textoBotonPrincipal,
            onClick = onImprimirPrueba,
            variant = WiButtonVariant.Primary,
            icon = WiIcons.Print,
            loading = isImprimiendo,
            modifier = Modifier.fillMaxWidth()
        )

        WiButton(
            text = if (isProbando) "Verificando Conexión Socket..." else "Comprobar Conexión (Ping)",
            onClick = onComprobarConexion,
            variant = WiButtonVariant.Secondary,
            icon = Icons.Rounded.Refresh,
            loading = isProbando,
            modifier = Modifier.fillMaxWidth()
        )

        WiButton(
            text = if (isEscaneando) "Buscando Impresoras en tu Wi-Fi..." else "Buscar Impresoras en mi Red",
            onClick = onEscanearRed,
            variant = WiButtonVariant.Secondary,
            icon = Icons.Rounded.Search,
            loading = isEscaneando,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
