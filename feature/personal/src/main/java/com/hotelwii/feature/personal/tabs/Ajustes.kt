package com.hotelwii.feature.personal.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiSwitch

/**
 * ⚙️ Tab 3: Ajustes.kt — Políticas de Seguridad, Permisos y Firma de Tickets del Personal.
 */
@Composable
fun Ajustes(
    onGuardarAjustes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var imprimirEnTicket by remember { mutableStateOf(true) }
    var requerirPinAnulacion by remember { mutableStateOf(false) }
    var permitirDescuentos by remember { mutableStateOf(false) }
    var autoCierreTurno by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ENCABEZADO
        item {
            Column {
                Text(
                    text = "Ajustes y Políticas de Personal",
                    style = WiText.h3,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Define cómo interactúan los colaboradores con la recepción y los comprobantes.",
                    style = WiText.small,
                    color = WiCss.tx3
                )
            }
        }

        // BLOQUE 1: IMPRESIÓN Y COMPROBANTES (3nStar)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(WiCss.wb)
                    .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "COMPROBANTES E IMPRESIÓN",
                        style = WiText.label,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )

                    WiSwitch(
                        checked = imprimirEnTicket,
                        onCheckedChange = { imprimirEnTicket = it },
                        label = "Imprimir Nombre en Ticket",
                        sublabel = "Muestra 'Atendido por: [Nombre]' en el ticket de la 3nStar."
                    )
                }
            }
        }

        // BLOQUE 2: SEGURIDAD Y PERMISOS EN RECEPCIÓN
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(WiCss.wb)
                    .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SEGURIDAD Y CONTROL",
                        style = WiText.label,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Bold
                    )

                    WiSwitch(
                        checked = requerirPinAnulacion,
                        onCheckedChange = { requerirPinAnulacion = it },
                        label = "Solicitar PIN para Anulaciones",
                        sublabel = "Exige el PIN al cancelar reservas o borrar cobros."
                    )

                    WiSwitch(
                        checked = permitirDescuentos,
                        onCheckedChange = { permitirDescuentos = it },
                        label = "Permitir Descuentos Libres",
                        sublabel = "Habilita a recepción modificar la tarifa base de las habitaciones."
                    )

                    WiSwitch(
                        checked = autoCierreTurno,
                        onCheckedChange = { autoCierreTurno = it },
                        label = "Cierre Automático de Turno",
                        sublabel = "Reinicia la sesión de tablet a las 00:00 horas."
                    )
                }
            }
        }

        item { Spacer(Modifier.height(10.dp)) }

        // BOTÓN GUARDAR AJUSTES
        item {
            WiButton(
                text = "Guardar Preferencias",
                onClick = onGuardarAjustes,
                variant = WiButtonVariant.Primary,
                icon = WiIcons.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}
