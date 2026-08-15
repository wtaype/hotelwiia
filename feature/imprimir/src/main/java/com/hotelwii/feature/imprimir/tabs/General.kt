package com.hotelwii.feature.imprimir.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
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
import com.hotelwii.feature.imprimir.components.Estados
import com.hotelwii.feature.imprimir.modelos.Prueba
import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.ImpresoraDetectada
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora
import com.hotelwii.feature.imprimir.servicios.TipoConexion

/**
 * 🖨️ General.kt — Tab 1 del Módulo Imprimir: Estado en Vivo y Parámetros Rápidos Visibles.
 */
@Composable
fun General(
    config: ModeloConfigImpresora,
    isConectadoLocal: Boolean = false,
    isConectadoNube: Boolean = false,
    isProbando: Boolean,
    isImprimiendo: Boolean,
    isEscaneando: Boolean,
    esReceptorActivo: Boolean = true,
    impresorasDetectadas: List<ImpresoraDetectada>,
    ultimoMensaje: String?,
    esError: Boolean,
    onEscanearRed: () -> Unit,
    onSeleccionarDetectada: (ImpresoraDetectada) -> Unit,
    onGuardarConfiguracion: (ModeloConfigImpresora) -> Unit,
    onComprobarConexion: () -> Unit,
    onImprimirBytes: (bytes: ByteArray, descripcion: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var ip by remember(config.ip) { mutableStateOf(config.ip.ifBlank { "192.168.0.110" }) }
    var puertoStr by remember(config.puerto) { mutableStateOf(config.puerto.toString()) }
    var anchoPapel by remember(config.anchoPapel) { mutableStateOf(config.anchoPapel) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 🌟 TARJETA 1: ESTADO EN VIVO Y DIAGNÓSTICO
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Estados(
                config = config,
                isConectadoLocal = isConectadoLocal,
                isConectadoNube = isConectadoNube,
                isProbando = isProbando,
                isImprimiendo = isImprimiendo,
                isEscaneando = isEscaneando,
                ultimoMensaje = ultimoMensaje,
                esError = esError,
                onEscanearRed = onEscanearRed,
                onComprobarConexion = onComprobarConexion,
                onImprimirPrueba = {
                    val bytes = Prueba.generar(
                        nombreHotel = "HOTEL WII & SUITES",
                        direccion = "Huacachina, Ica - Perú",
                        ruc = "20601234567",
                        anchoPapel = config.anchoPapel,
                        ipOpcional = config.ip
                    )
                    onImprimirBytes(bytes, "Ticket de Prueba")
                }
            )
        }

        // ==========================================
        // 🔍 LISTA DE IMPRESORAS DETECTADAS (SI HAY)
        // ==========================================
        if (impresorasDetectadas.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiCss.wb)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Impresoras Detectadas en tu Red (${impresorasDetectadas.size}):",
                        style = WiText.label,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )

                    impresorasDetectadas.forEach { imp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(WiCss.inp)
                                .clickable { onSeleccionarDetectada(imp) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = WiIcons.Print,
                                    contentDescription = null,
                                    tint = WiCss.hv,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${imp.nombreSugerido} (${imp.ip}:${imp.puerto})",
                                        style = WiText.small,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Puerto ESC/POS abierto · Toca para vincular",
                                        style = WiText.tiny,
                                        color = WiCss.tx2
                                    )
                                }
                            }

                            WiButton(
                                text = "Vincular",
                                onClick = { onSeleccionarDetectada(imp) },
                                variant = WiButtonVariant.Primary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // ⚙️ TARJETA 2: PARÁMETROS DE RED DIRECTAMENTE VISIBLES
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Configuración de Conexión y Red",
                    style = WiText.h3,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Modifica la IP y puerto de la impresora térmica según la red del hotel.",
                    style = WiText.small,
                    color = WiCss.tx2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = "Dirección IP (3nStar)",
                        modifier = Modifier.weight(2f)
                    )

                    WiField(
                        value = puertoStr,
                        onValueChange = { puertoStr = it },
                        label = "Puerto",
                        modifier = Modifier.weight(1f)
                    )
                }

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

                WiButton(
                    text = "Guardar y Aplicar IP",
                    onClick = {
                        val p = puertoStr.toIntOrNull() ?: 9100
                        val nuevaConfig = config.copy(
                            tipoConexion = TipoConexion.RED_TCP,
                            ip = ip.ifBlank { "192.168.0.110" },
                            puerto = p,
                            anchoPapel = anchoPapel,
                            estaConfigurada = true
                        )
                        onGuardarConfiguracion(nuevaConfig)
                    },
                    variant = WiButtonVariant.Primary,
                    icon = Icons.Rounded.Check,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
