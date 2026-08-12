package com.hotelwii.feature.auth.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GlassCard
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.feature.auth.lib.Serializar

/**
 * 👤 GoogleProfileModal.kt — Modal para completar Username y Términos en registros nuevos con Google en HotelWii.
 */
@Composable
fun GoogleProfileModal(
    googleEmail: String,
    onCompletarRegistro: (username: String, aceptoTerminos: Boolean) -> Unit,
    onCancelar: () -> Unit,
    isLoading: Boolean = false
) {
    val uriHandler = LocalUriHandler.current
    var username by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Completa tu Perfil Google",
                style = WiText.h3,
                color = WiCss.mco,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Tu correo $googleEmail está autenticado por Google. Elige tu usuario para vincular tu cuenta en HotelWii.",
                style = WiText.small,
                color = WiCss.tx3,
                textAlign = TextAlign.Center
            )

            // Campo Nombre de Usuario Sanitizado en tiempo real
            WiField(
                value = username,
                onValueChange = { username = Serializar.usuario(it) },
                label = "Nombre de Usuario Único",
                leadingIcon = Icons.Rounded.Person,
                modifier = Modifier.fillMaxWidth()
            )

            // Checkbox Términos y Condiciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = WiCss.mco,
                        uncheckedColor = WiCss.brd
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Acepto los ",
                    style = WiText.small,
                    color = WiCss.tx3
                )
                Text(
                    text = "Términos y Condiciones",
                    style = WiText.small,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://hotelwii.com/terminos")
                    }
                )
            }

            WiButton(
                text = if (isLoading) "Guardando..." else "Completar Registro",
                onClick = {
                    if (username.isNotBlank() && aceptoTerminos) {
                        onCompletarRegistro(username, aceptoTerminos)
                    }
                },
                loading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            WiButton(
                text = "Cancelar",
                onClick = onCancelar,
                containerColor = WiCss.inp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
