package com.hotelwii.feature.imprimir.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora

/**
 * Ajustes — Tab 3 del Módulo Imprimir: Opciones avanzadas de hardware y tickets.
 */
@Composable
fun Ajustes(
    config: ModeloConfigImpresora,
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
                text = "Configura el formato de bobina térmica, corte automático y apertura de gaveta.",
                style = WiText.small,
                color = WiCss.tx2
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
                sublabel = "Ejecuta el comando de corte al finalizar la impresión."
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
        }
    }
}
