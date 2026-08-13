package com.hotelwii.feature.empresas.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.feature.empresas.components.FormularioEmpresa
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * ➕ NuevoEmpresa.kt — Pestaña 2: Formulario limpio de Registro y Edición de Hotel.
 */
@Composable
fun NuevoEmpresa(
    empresaExistente: ModeloEmpresa? = null,
    isBuscandoRuc: Boolean = false,
    isGuardando: Boolean = false,
    onConsultarRuc: (String, (String, String, String, String, String, String, String) -> Unit) -> Unit,
    onGuardar: (ModeloEmpresa) -> Unit,
    onCancelar: () -> Unit = {},
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
        FormularioEmpresa(
            empresaExistente = empresaExistente,
            isBuscandoRuc = isBuscandoRuc,
            isGuardando = isGuardando,
            onConsultarRuc = onConsultarRuc,
            onGuardar = onGuardar,
            onCancelar = onCancelar
        )
    }
}
