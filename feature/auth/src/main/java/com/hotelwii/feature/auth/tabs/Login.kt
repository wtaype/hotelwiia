package com.hotelwii.feature.auth.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GlassCard
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiPassword
import com.hotelwii.feature.auth.components.GoogleButton
import com.hotelwii.feature.auth.lib.Serializar

/**
 * 🔑 Login.kt — Formulario de Ingreso para HotelWii con Sanitización en Tiempo Real (`Serializar.kt`).
 */
@Composable
fun Login(
    onIngresar: (emailOrUser: String, pass: String) -> Unit,
    onGoogleAuth: () -> Unit,
    onIrARegistro: () -> Unit,
    isLoading: Boolean = false
) {
    var emailOrUser by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 🌐 1. Google 1-Click Access Button
            GoogleButton(
                onClick = onGoogleAuth,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // ────── Divisor Visual ──────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = WiCss.brd.copy(alpha = 0.4f))
                Text(
                    text = "  ó ingresa con correo  ",
                    style = WiText.small,
                    color = WiCss.tx3,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = WiCss.brd.copy(alpha = 0.4f))
            }

            // 📧 2. Campo Usuario o Correo
            WiField(
                value = emailOrUser,
                onValueChange = { emailOrUser = Serializar.email(it) },
                label = "Correo o Usuario",
                leadingIcon = Icons.Rounded.Email,
                modifier = Modifier.fillMaxWidth()
            )

            // 🔒 3. Campo Contraseña
            WiPassword(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            // 🚀 4. Botón Maestro de Ingreso
            WiButton(
                text = if (isLoading) "Ingresando..." else "Ingresar al Sistema",
                onClick = { onIngresar(emailOrUser, password) },
                loading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // 🔗 5. Enlaces Secundarios
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* Recuperar */ }) {
                    Text(
                        text = "¿Olvidaste contraseña?",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }

                TextButton(onClick = onIrARegistro) {
                    Text(
                        text = "Crear cuenta ➔",
                        style = WiText.small,
                        color = WiCss.mco,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
