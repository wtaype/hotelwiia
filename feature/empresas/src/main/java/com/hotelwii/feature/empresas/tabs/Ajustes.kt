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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * ⚙️ Ajustes.kt — Pestaña 3: Configuración de Facturación y Comprobantes SUNAT del Hotel Seleccionado.
 * Incorpora WiSelect para conmutar entre cualquier hotel del usuario y ajustar su facturación en tiempo real.
 */
@Composable
fun Ajustes(
    empresas: List<ModeloEmpresa>,
    hotelSeleccionado: ModeloEmpresa?,
    onSeleccionarHotel: (ModeloEmpresa) -> Unit,
    isGuardando: Boolean = false,
    onGuardarAjustes: (empresaId: String, notaVenta: Boolean, boleta: Boolean, factura: Boolean, impuesto: Double, moneda: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var notaVenta by remember(hotelSeleccionado) { mutableStateOf(hotelSeleccionado?.notaVenta ?: true) }
    var boleta by remember(hotelSeleccionado) { mutableStateOf(hotelSeleccionado?.boleta ?: true) }
    var factura by remember(hotelSeleccionado) { mutableStateOf(hotelSeleccionado?.factura ?: true) }
    var impuestoStr by remember(hotelSeleccionado) { mutableStateOf((hotelSeleccionado?.impuestoPorcentaje ?: 18.00).toString()) }
    var moneda by remember(hotelSeleccionado) { mutableStateOf(hotelSeleccionado?.moneda ?: "PEN") }

    val scrollState = rememberScrollState()

    val opcionesHoteles = remember(empresas) {
        empresas.map { it.nombreComercial.ifBlank { "Hotel Sin Nombre" } }
    }
    val nombreHotelSeleccionado = hotelSeleccionado?.nombreComercial?.ifBlank { "Hotel Sin Nombre" } ?: "Seleccionar Hotel"

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

                if (hotelSeleccionado?.principal == true) {
                    GoldPill(text = "HOTEL ACTIVO")
                }
            }

            Text(
                text = "Selecciona el hotel para configurar sus comprobantes permitidos en recepción y parámetros tributarios.",
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

            // 2. Switch Nota de Venta
            AjusteSwitchRow(
                titulo = "Notas de Venta Internas",
                subtitulo = "Comprobantes de consumo interno para huéspedes (sin enviar a SUNAT)",
                checked = notaVenta,
                onCheckedChange = { notaVenta = it }
            )

            // 3. Switch Boleta Electrónica
            AjusteSwitchRow(
                titulo = "Boletas Electrónicas (B001)",
                subtitulo = "Emisión de boletas electrónicas oficiales a personas naturales con DNI",
                checked = boleta,
                onCheckedChange = { boleta = it }
            )

            // 4. Switch Factura Electrónica
            AjusteSwitchRow(
                titulo = "Facturas Electrónicas (F001)",
                subtitulo = "Emisión de facturas electrónicas oficiales a empresas con RUC",
                checked = factura,
                onCheckedChange = { factura = it }
            )

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
                text = "Guardar Ajustes de Facturación",
                onClick = {
                    val empId = hotelSeleccionado?.id ?: return@WiButton
                    val imp = impuestoStr.toDoubleOrNull() ?: 18.00
                    onGuardarAjustes(empId, notaVenta, boleta, factura, imp, moneda)
                },
                loading = isGuardando,
                icon = Icons.Rounded.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AjusteSwitchRow(
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = WiText.body,
                color = WiCss.tx1,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitulo,
                style = WiText.small,
                color = WiCss.tx3
            )
        }

        Spacer(Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WiCss.mco,
                checkedTrackColor = WiCss.mco.copy(alpha = 0.3f),
                uncheckedThumbColor = WiCss.tx3,
                uncheckedTrackColor = WiCss.inp
            )
        )
    }
}
