package com.hotelwii.feature.imprimir.tabs

import androidx.compose.foundation.background
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
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.imprimir.data.ModeloImpresion
import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora

/**
 * ⚙️ Ajustes — Tab 3 del Módulo Imprimir: Opciones avanzadas de hardware, Receptor Realtime e Historial.
 */
@Composable
fun Ajustes(
    config: ModeloConfigImpresora,
    esReceptorActivo: Boolean = true,
    historial: List<ModeloImpresion> = emptyList(),
    onToggleReceptor: (Boolean) -> Unit = {},
    onGuardarConfiguracion: (ModeloConfigImpresora) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Ajustes de Impresión",
                style = WiText.h3,
                color = WiCss.tx1,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Configura el formato de bobina térmica, corte automático y recepción en tiempo real.",
                style = WiText.small,
                color = WiCss.tx2
            )

            // Switch de Receptor en la Nube
            WiSwitch(
                checked = esReceptorActivo,
                onCheckedChange = onToggleReceptor,
                label = "Servidor Receptor en este Dispositivo",
                sublabel = "Escucha Supabase Realtime e imprime los comprobantes enviados desde celulares remotos."
            )

            WiSelect(
                options = listOf("80 mm (48 columnas - Estándar 3nStar)", "58 mm (32 columnas - Mini POS)"),
                selectedOption = if (config.anchoPapel == AnchoPapel.PAPEL_80MM) {
                    "80 mm (48 columnas - Estándar 3nStar)"
                } else {
                    "58 mm (32 columnas - Mini POS)"
                },
                onOptionSelected = { op ->
                    val ancho = if (op.startsWith("80")) AnchoPapel.PAPEL_80MM else AnchoPapel.PAPEL_58MM
                    onGuardarConfiguracion(config.copy(anchoPapel = ancho))
                },
                label = "Ancho de Bobina Térmica"
            )

            WiSwitch(
                checked = config.cortarPapel,
                onCheckedChange = { onGuardarConfiguracion(config.copy(cortarPapel = it)) },
                label = "Corte Automático de Papel (Auto-Cutter)",
                sublabel = "Ejecuta el comando ESC/POS de corte al finalizar cada ticket."
            )

            WiSwitch(
                checked = config.abrirCajon,
                onCheckedChange = { onGuardarConfiguracion(config.copy(abrirCajon = it)) },
                label = "Apertura de Gaveta de Dinero",
                sublabel = "Envía pulso eléctrico al puerto RJ11 de la ticketera para abrir el cajón."
            )

            WiSelect(
                options = listOf("1 Copia (Recepción)", "2 Copias (Huésped + Control Hotel)"),
                selectedOption = if (config.numCopias == 2) "2 Copias (Huésped + Control Hotel)" else "1 Copia (Recepción)",
                onOptionSelected = { op ->
                    val copias = if (op.startsWith("2")) 2 else 1
                    onGuardarConfiguracion(config.copy(numCopias = copias))
                },
                label = "Copias por Emisión"
            )

            if (historial.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Historial Reciente de Emisiones (${historial.size}):",
                    style = WiText.label,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    historial.take(6).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(WiCss.inp.copy(alpha = 0.5f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = WiIcons.Print,
                                    contentDescription = null,
                                    tint = WiCss.hv,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.titulo.ifBlank { item.tipo.uppercase() },
                                        style = WiText.small,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${item.impresoPor.ifBlank { "Recepcionista" }} · ${item.estado.uppercase()}",
                                        style = WiText.tiny,
                                        color = if (item.estado == "impreso") WiCss.success else WiCss.tx3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
