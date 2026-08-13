package com.hotelwii.feature.empresas.tabs

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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
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
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * ⚙️ Ajustes.kt — Pestaña 3: Configuración Fiscal SUNAT y Series del Hotel Seleccionado.
 * Incorpora WiSelect para conmutar entre cualquier hotel del usuario y switches reactivos 0ms.
 */
@Composable
fun Ajustes(
    empresas: List<ModeloEmpresa>,
    hotelSeleccionado: ModeloEmpresa?,
    onSeleccionarHotel: (ModeloEmpresa) -> Unit,
    isGuardando: Boolean = false,
    onGuardarAjustes: (
        empresaId: String,
        notaVenta: Boolean,
        boleta: Boolean,
        factura: Boolean,
        serieBoleta: String,
        serieFactura: String,
        serieNota: String,
        impuesto: Double,
        moneda: String
    ) -> Unit,
    onToggleCampo: (empresa: ModeloEmpresa, campo: String, nuevoValor: Boolean) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    if (empresas.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registra un hotel primero para configurar sus parámetros fiscales SUNAT.",
                style = WiText.body,
                color = WiCss.tx3
            )
        }
        return
    }

    val hotelActual = hotelSeleccionado ?: empresas.first()

    var notaVenta by remember(hotelActual) { mutableStateOf(hotelActual.notaVenta) }
    var boleta by remember(hotelActual) { mutableStateOf(hotelActual.boleta) }
    var factura by remember(hotelActual) { mutableStateOf(hotelActual.factura) }
    var serieBoleta by remember(hotelActual) { mutableStateOf(hotelActual.serieBoleta) }
    var serieFactura by remember(hotelActual) { mutableStateOf(hotelActual.serieFactura) }
    var serieNota by remember(hotelActual) { mutableStateOf(hotelActual.serieNota) }
    var impuestoStr by remember(hotelActual) { mutableStateOf(hotelActual.impuestoPorcentaje.toString()) }
    var moneda by remember(hotelActual) { mutableStateOf(hotelActual.moneda) }

    val scrollState = rememberScrollState()

    val opcionesHoteles = remember(empresas) {
        empresas.map { it.nombreComercial.ifBlank { "Hotel Sin Nombre" } }
    }
    val nombreHotelSeleccionado = hotelActual.nombreComercial.ifBlank { "Hotel Sin Nombre" }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Pestaña Ajustes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ajustes de Facturación SUNAT",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (hotelActual.principal) {
                    GoldPill(text = "HOTEL ACTIVO")
                }
            }

            Text(
                text = "Selecciona el hotel para configurar sus comprobantes permitidos y series de emisión SUNAT:",
                style = WiText.small,
                color = WiCss.tx3
            )

            // 1. Selector de Hotel con WiSelect
            if (opcionesHoteles.isNotEmpty()) {
                WiSelect(
                    selectedOption = nombreHotelSeleccionado,
                    options = opcionesHoteles,
                    onOptionSelected = { nombreSeleccionado ->
                        val encontrado = empresas.firstOrNull { it.nombreComercial == nombreSeleccionado }
                        if (encontrado != null) {
                            onSeleccionarHotel(encontrado)
                        }
                    },
                    label = "Hotel a Configurar",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(2.dp))

            // 2. Switches Apple Pro Reactivos (0ms)
            WiSwitch(
                checked = hotelActual.notaVenta,
                onCheckedChange = { nuevoValor ->
                    onToggleCampo(hotelActual, "nota_venta", nuevoValor)
                },
                label = "Notas de Venta Internas",
                sublabel = "Serie predeterminada: ${hotelActual.serieNota} • Comprobantes de consumo interno",
                activeTrackColor = WiCss.success
            )

            WiSwitch(
                checked = hotelActual.boleta,
                onCheckedChange = { nuevoValor ->
                    onToggleCampo(hotelActual, "boleta", nuevoValor)
                },
                label = "Boletas de Venta Electrónicas",
                sublabel = "Serie predeterminada: ${hotelActual.serieBoleta} • Emisión oficial a DNI",
                activeTrackColor = WiCss.success
            )

            WiSwitch(
                checked = hotelActual.factura,
                onCheckedChange = { nuevoValor ->
                    onToggleCampo(hotelActual, "factura", nuevoValor)
                },
                label = "Facturas Electrónicas RUC",
                sublabel = "Serie predeterminada: ${hotelActual.serieFactura} • Emisión oficial a RUC",
                activeTrackColor = WiCss.mco
            )

            Spacer(Modifier.height(4.dp))

            // 📑 Sección Series SUNAT Editables
            Text(
                text = "Series de Comprobantes SUNAT",
                style = WiText.body,
                color = WiCss.tx1,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WiField(
                    value = serieNota,
                    onValueChange = { serieNota = it.uppercase() },
                    label = "Serie Nota",
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = serieBoleta,
                    onValueChange = { serieBoleta = it.uppercase() },
                    label = "Serie Boleta",
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = serieFactura,
                    onValueChange = { serieFactura = it.uppercase() },
                    label = "Serie Factura",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(4.dp))

            // Fila de Impuestos & Moneda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WiField(
                    value = impuestoStr,
                    onValueChange = { impuestoStr = it },
                    label = "Tasa de IGV / Impuesto (%)",
                    leadingIcon = Icons.Rounded.Info,
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = moneda,
                    onValueChange = { moneda = it.uppercase() },
                    label = "Moneda Principal",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Botón Guardar Ajustes
            WiButton(
                text = "Guardar Ajustes y Series SUNAT",
                onClick = {
                    val empId = hotelActual.id ?: return@WiButton
                    val imp = impuestoStr.toDoubleOrNull() ?: 18.00
                    onGuardarAjustes(
                        empId,
                        hotelActual.notaVenta,
                        hotelActual.boleta,
                        hotelActual.factura,
                        serieBoleta,
                        serieFactura,
                        serieNota,
                        imp,
                        moneda
                    )
                },
                loading = isGuardando,
                icon = Icons.Rounded.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
