package com.hotelwii.feature.empresas.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GlassCard
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiMain
import com.hotelwii.core.kidev.wiSwipe
import com.hotelwii.feature.empresas.cards.EmpresaCard
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * 🏨 MisEmpresas.kt — Pestaña 1: Lista de Hoteles con selector 1-Tap, Pull-to-Refresh (wiSwipe) y Onboarding Glass.
 */
@Composable
fun MisEmpresas(
    empresas: List<ModeloEmpresa>,
    hotelActivoId: String?,
    onSeleccionar: (ModeloEmpresa) -> Unit,
    onEditar: (ModeloEmpresa) -> Unit,
    onEliminar: (ModeloEmpresa) -> Unit,
    onRefrescar: () -> Unit = {},
    isRefreshing: Boolean = false,
    onIrANuevo: () -> Unit,
    modifier: Modifier = Modifier
) {
    WiMain(
        modifier = modifier.wiSwipe(
            onDown = { _, _ -> onRefrescar() }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera No Invasiva con Acción Rápida y Refresco
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mis Hoteles Registrados",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Desliza hacia abajo para actualizar o conmuta tu hotel activo:",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }

                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = WiCss.mco,
                        strokeWidth = 2.5.dp
                    )
                }
            }

            if (empresas.isEmpty()) {
                GlassCard(
                    onClick = onIrANuevo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¡Bienvenido a HotelWii!",
                        style = WiText.h3,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Aún no tienes ningún hotel registrado. Haz clic aquí para registrar tu primer hotel en Perú.",
                        style = WiText.body,
                        color = WiCss.tx2
                    )
                }
            } else {
                empresas.forEach { emp ->
                    val isActiva = emp.id != null && emp.id == hotelActivaIdResolved(emp, hotelActivoId)
                    EmpresaCard(
                        empresa = emp,
                        isActiva = isActiva,
                        onSeleccionar = onSeleccionar,
                        onEditar = onEditar,
                        onEliminar = onEliminar
                    )
                }
            }

            // Botón de Acción Elegante de Fondo sin Solapamientos
            WiButton(
                text = "+ Registrar Nuevo Hotel",
                onClick = onIrANuevo,
                icon = Icons.Rounded.Add,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun hotelActivaIdResolved(emp: ModeloEmpresa, hotelActivoId: String?): String? {
    if (!hotelActivoId.isNullOrBlank()) return hotelActivoId
    if (emp.principal) return emp.id
    return null
}
