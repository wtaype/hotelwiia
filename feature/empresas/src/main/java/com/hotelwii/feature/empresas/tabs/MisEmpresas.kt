package com.hotelwii.feature.empresas.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GlassCard
import com.hotelwii.feature.empresas.cards.EmpresaCard
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * 🏨 MisEmpresas.kt — Pestaña 1: Lista de Hoteles/Empresas con selector de Hotel Activo en 1-Tap.
 */
@Composable
fun MisEmpresas(
    empresas: List<ModeloEmpresa>,
    hotelActivoId: String?,
    onSeleccionar: (ModeloEmpresa) -> Unit,
    onEditar: (ModeloEmpresa) -> Unit,
    onEliminar: (ModeloEmpresa) -> Unit,
    onIrANuevo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                    text = "Aún no tienes ningún hotel registrado. Haz clic aquí para registrar tu primer hotel.",
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
    }
}

private fun hotelActivaIdResolved(emp: ModeloEmpresa, hotelActivoId: String?): String? {
    if (!hotelActivoId.isNullOrBlank()) return hotelActivoId
    if (emp.principal) return emp.id
    return null
}
