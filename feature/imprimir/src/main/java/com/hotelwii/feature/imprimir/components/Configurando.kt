package com.hotelwii.feature.imprimir.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
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
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.ImpresoraDetectada
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora
import com.hotelwii.feature.imprimir.servicios.TipoConexion

/**
 * Configurando — Asistente visual estructurado para vinculación, escaneo Wi-Fi y setup de ticketera 3nStar.
 */
@Composable
fun Configurando(
    configActual: ModeloConfigImpresora,
    isEscaneando: Boolean,
    impresorasDetectadas: List<ImpresoraDetectada>,
    onEscanearRed: () -> Unit,
    onSeleccionarDetectada: (ImpresoraDetectada) -> Unit,
    onGuardar: (ModeloConfigImpresora) -> Unit,
    onCancelar: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var tipoConexion by remember { mutableStateOf(configActual.tipoConexion) }
    var ip by remember { mutableStateOf(configActual.ip) }
    var puertoStr by remember { mutableStateOf(configActual.puerto.toString()) }
    var macBluetooth by remember { mutableStateOf(configActual.macBluetooth) }
    var anchoPapel by remember { mutableStateOf(configActual.anchoPapel) }

    var mostrarGuiaAyuda by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Título Principal Estilo Empresas
        Text(
            text = "Configuración de Impresora",
            style = WiText.h3,
            color = WiCss.tx1,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Vincula la impresora de tickets de recepción mediante escaneo automático o ingresando su IP de red.",
            style = WiText.small,
            color = WiCss.tx2
        )

        // 🔍 Botón de Escaneo Automático en Red Local
        WiButton(
            text = if (isEscaneando) "Buscando en la Red Wi-Fi..." else "Buscar Impresoras en mi Red",
            onClick = onEscanearRed,
            variant = WiButtonVariant.Primary,
            icon = if (isEscaneando) null else Icons.Rounded.Search,
            loading = isEscaneando,
            modifier = Modifier.fillMaxWidth()
        )

        // Lista de Impresoras Encontradas en la Red
        if (impresorasDetectadas.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WiCss.mco.copy(alpha = 0.08f))
                    .border(1.dp, WiCss.mco.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = WiIcons.Wifi,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Impresoras detectadas en tu red:",
                        style = WiText.label,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                }

                impresorasDetectadas.forEach { detectada ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WiCss.wb)
                            .border(1.dp, WiCss.brd, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "IP: ${detectada.ip}:${detectada.puerto}",
                                style = WiText.body,
                                color = WiCss.tx1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Respuesta: ${detectada.tiempoRespuestaMs} ms · Protocolo ESC/POS",
                                style = WiText.tiny,
                                color = WiCss.tx2
                            )
                        }

                        WiButton(
                            text = "Conectar",
                            onClick = {
                                ip = detectada.ip
                                puertoStr = detectada.puerto.toString()
                                onSeleccionarDetectada(detectada)
                            },
                            variant = WiButtonVariant.Primary,
                            icon = Icons.Rounded.Check
                        )
                    }
                }
            }
        }

        // 📘 Tarjeta Desplegable: Guía Rápida de Conexión 3nStar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(WiCss.inp.copy(alpha = 0.5f))
                .border(1.dp, WiCss.brd, RoundedCornerShape(14.dp))
                .clickable { mostrarGuiaAyuda = !mostrarGuiaAyuda }
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "¿Cómo saber la IP exacta de tu 3nStar?",
                            style = WiText.small,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = if (mostrarGuiaAyuda) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WiCss.tx2,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(
                    visible = mostrarGuiaAyuda,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "1. Apaga la impresora desde el interruptor de encendido.",
                            style = WiText.small,
                            color = WiCss.tx2
                        )
                        Text(
                            text = "2. Mantén presionado el botón FEED (Alimentación) y enciende la impresora sin soltar el botón.",
                            style = WiText.small,
                            color = WiCss.tx2
                        )
                        Text(
                            text = "3. Suelta el botón tras 3 segundos. La ticketera imprimirá una hoja de Auto-Prueba (Self-Test).",
                            style = WiText.small,
                            color = WiCss.tx2
                        )
                        Text(
                            text = "4. En el papel busca la línea 'IP Address' (ej: 192.168.1.100).",
                            style = WiText.small,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "5. Verifica que tu teléfono esté en el mismo Wi-Fi del router y pulsa 'Buscar Impresoras en mi Red'.",
                            style = WiText.small,
                            color = WiCss.tx2
                        )
                    }
                }
            }
        }

        // Selector Tipo de Conexión
        WiSelect(
            options = listOf("Red Ethernet / Wi-Fi (Socket TCP)", "Bluetooth SPP"),
            selectedOption = when (tipoConexion) {
                TipoConexion.RED_TCP -> "Red Ethernet / Wi-Fi (Socket TCP)"
                TipoConexion.BLUETOOTH -> "Bluetooth SPP"
            },
            onOptionSelected = { op ->
                tipoConexion = if (op.contains("Bluetooth")) TipoConexion.BLUETOOTH else TipoConexion.RED_TCP
            },
            label = "Tipo de Conexión"
        )

        // Campos según el tipo de conexión
        if (tipoConexion == TipoConexion.RED_TCP) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WiField(
                    value = ip,
                    onValueChange = { ip = it.trim() },
                    label = "Dirección IP (3nStar)",
                    modifier = Modifier.weight(2f)
                )

                WiField(
                    value = puertoStr,
                    onValueChange = { puertoStr = it.trim() },
                    label = "Puerto (9100)",
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            WiField(
                value = macBluetooth,
                onValueChange = { macBluetooth = it.trim() },
                label = "Dirección MAC Bluetooth (ej: 00:11:22:33:44:55)",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Ancho de Papel
        WiSelect(
            options = listOf("80 mm (48 columnas - Estándar 3nStar)", "58 mm (32 columnas - Mini POS)"),
            selectedOption = if (anchoPapel == AnchoPapel.PAPEL_80MM) {
                "80 mm (48 columnas - Estándar 3nStar)"
            } else {
                "58 mm (32 columnas - Mini POS)"
            },
            onOptionSelected = { op ->
                anchoPapel = if (op.startsWith("80")) AnchoPapel.PAPEL_80MM else AnchoPapel.PAPEL_58MM
            },
            label = "Formato de Papel Térmico"
        )

        Spacer(Modifier.height(4.dp))

        // Botones
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            WiButton(
                text = "Guardar y Conectar",
                onClick = {
                    val p = puertoStr.toIntOrNull() ?: 9100
                    val nuevaConfig = configActual.copy(
                        tipoConexion = tipoConexion,
                        ip = ip.ifBlank { "192.168.1.100" },
                        puerto = p,
                        macBluetooth = macBluetooth,
                        anchoPapel = anchoPapel,
                        estaConfigurada = true
                    )
                    onGuardar(nuevaConfig)
                },
                variant = WiButtonVariant.Primary,
                icon = Icons.Rounded.Check,
                modifier = Modifier.fillMaxWidth()
            )

            if (onCancelar != null) {
                WiButton(
                    text = "Cancelar",
                    onClick = onCancelar,
                    variant = WiButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
