package com.hotelwii.feature.auth.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GlassCard
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiPassword
import com.hotelwii.feature.auth.components.GoogleButton
import com.hotelwii.feature.auth.lib.Serializar
import com.hotelwii.feature.auth.lib.Validar

/**
 * 📝 Registro.kt — Formulario de Registro para HotelWii con Validaciones en Tiempo Real (`Validar.kt`).
 */
@Composable
fun Registro(
    onRegistrar: (email: String, pass: String, usuario: String, nombre: String, apellidos: String, aceptoTerminos: Boolean) -> Unit,
    onGoogleAuth: () -> Unit,
    onIrALogin: () -> Unit,
    isLoading: Boolean = false
) {
    val uriHandler = LocalUriHandler.current

    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }

    // Estados de Validación en tiempo real
    val isNombreOk = Validar.esNombreValido(nombre)
    val isApellidosOk = Validar.esNombreValido(apellidos)
    val isEmailOk = Validar.esEmailValido(email)
    val isUsuarioOk = Validar.esUsuarioValido(usuario)
    val isPasswordOk = Validar.esPasswordValida(password)
    val isConfirmPassOk = Validar.coincidenPasswords(password, confirmPassword)

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    text = "  ó regístrate con correo  ",
                    style = WiText.small,
                    color = WiCss.tx3,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = WiCss.brd.copy(alpha = 0.4f))
            }

            // 👥 FILA 1 (2 COLUMNAS): Nombre | Apellidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WiField(
                    value = nombre,
                    onValueChange = { nombre = Serializar.nombre(it) },
                    label = "Nombre",
                    leadingIcon = Icons.Rounded.Person,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    isSuccess = nombre.isNotBlank() && isNombreOk,
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = apellidos,
                    onValueChange = { apellidos = Serializar.nombre(it) },
                    label = "Apellidos",
                    leadingIcon = Icons.Rounded.Person,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    isSuccess = apellidos.isNotBlank() && isApellidosOk,
                    modifier = Modifier.weight(1f)
                )
            }

            // 📧 FILA 2: Correo Electrónico
            WiField(
                value = email,
                onValueChange = { email = Serializar.email(it) },
                label = "Correo Electrónico",
                leadingIcon = Icons.Rounded.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isSuccess = email.isNotBlank() && isEmailOk,
                isError = email.isNotBlank() && !isEmailOk,
                errorMessage = if (email.isNotBlank() && !isEmailOk) "Ingresa un correo válido" else null,
                modifier = Modifier.fillMaxWidth()
            )

            // 👤 FILA 3: Nombre de Usuario
            WiField(
                value = usuario,
                onValueChange = { usuario = Serializar.usuario(it) },
                label = "Nombre de Usuario",
                leadingIcon = Icons.Rounded.Person,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii
                ),
                isSuccess = usuario.isNotBlank() && isUsuarioOk,
                isError = usuario.isNotBlank() && !isUsuarioOk,
                errorMessage = if (usuario.isNotBlank() && !isUsuarioOk) "Mínimo 3 caracteres (a-z, 0-9, _)" else null,
                modifier = Modifier.fillMaxWidth()
            )

            // 🔒 FILA 4: Contraseña
            WiPassword(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                isSuccess = password.isNotBlank() && isPasswordOk,
                isError = password.isNotBlank() && !isPasswordOk,
                errorMessage = if (password.isNotBlank() && !isPasswordOk) "Mínimo 6 caracteres requeridos" else null,
                modifier = Modifier.fillMaxWidth()
            )

            // 🔒 FILA 5: Confirmar Contraseña
            WiPassword(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar Contraseña",
                isSuccess = confirmPassword.isNotBlank() && isConfirmPassOk,
                isError = confirmPassword.isNotBlank() && !isConfirmPassOk,
                errorMessage = if (confirmPassword.isNotBlank() && !isConfirmPassOk) "Las contraseñas no coinciden" else null,
                modifier = Modifier.fillMaxWidth()
            )

            // 📜 FILA 6: Checkbox Términos & Condiciones
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

            // 🚀 Botón Registrarme
            val isFormularioValido = isEmailOk && isUsuarioOk && isPasswordOk && isConfirmPassOk && aceptoTerminos

            WiButton(
                text = if (isLoading) "Registrando..." else "Registrar Cuenta HotelWii",
                onClick = {
                    if (isFormularioValido) {
                        onRegistrar(email, password, usuario, nombre, apellidos, aceptoTerminos)
                    }
                },
                loading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Enlace rápido para ir a Login
            TextButton(onClick = onIrALogin) {
                Text(
                    text = "¿Ya tienes cuenta? Ingresar aquí",
                    style = WiText.small,
                    color = WiCss.tx3
                )
            }
        }
    }
}
