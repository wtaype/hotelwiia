package com.hotelwii.feature.cuenta.tabs

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
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
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiPassword
import com.hotelwii.feature.cuenta.CuentaUiState

/**
 * 🔒 Seguridad.kt — Pestaña 3: Cambio de Contraseña & Estado de Verificación de Cuenta.
 */
@Composable
fun Seguridad(
    uiState: CuentaUiState
) {
    var passNueva by remember { mutableStateOf("") }
    var passConfirmar by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Estado & Verificación de Cuenta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = WiCss.success,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Estado & Verificación de Cuenta",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = WiCss.success,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Registrado mediante ${uiState.smile?.registradoPor?.uppercase() ?: "CORREO"}",
                        style = WiText.body,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = WiCss.success,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Términos y condiciones aceptados",
                        style = WiText.body,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 2. Cambiar Contraseña
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cambiar Contraseña",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                WiPassword(
                    value = passNueva,
                    onValueChange = { passNueva = it },
                    label = "Nueva Contraseña",
                    modifier = Modifier.fillMaxWidth()
                )

                WiPassword(
                    value = passConfirmar,
                    onValueChange = { passConfirmar = it },
                    label = "Confirmar Nueva Contraseña",
                    modifier = Modifier.fillMaxWidth()
                )

                if (mensajeError != null) {
                    Text(
                        text = mensajeError!!,
                        style = WiText.small,
                        color = WiCss.error
                    )
                }

                if (mensajeExito != null) {
                    Text(
                        text = mensajeExito!!,
                        style = WiText.small,
                        color = WiCss.success
                    )
                }

                WiButton(
                    text = "Actualizar Clave",
                    onClick = {
                        if (passNueva.length < 6) {
                            mensajeError = "La contraseña debe tener al menos 6 caracteres"
                            mensajeExito = null
                        } else if (passNueva != passConfirmar) {
                            mensajeError = "Las contraseñas no coinciden"
                            mensajeExito = null
                        } else {
                            mensajeError = null
                            mensajeExito = "Contraseña actualizada exitosamente"
                            passNueva = ""
                            passConfirmar = ""
                        }
                    },
                    icon = Icons.Rounded.Lock,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
