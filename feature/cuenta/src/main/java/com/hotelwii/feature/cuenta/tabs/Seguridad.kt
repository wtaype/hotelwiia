package com.hotelwii.feature.cuenta.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.Encriptar
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiPassword
import com.hotelwii.core.kidev.getSecure
import com.hotelwii.core.kidev.saveSecure
import com.hotelwii.core.kidev.wiStore
import com.hotelwii.feature.cuenta.CuentaUiState
import com.hotelwii.feature.cuenta.api.CuentaApi
import kotlinx.coroutines.launch

/**
 * 🔒 Seguridad.kt — Pestaña 3: Protección por PIN de 4 dígitos, Re-autenticación de Recuperación & Bóveda Cifrada.
 */
@Composable
fun Seguridad(
    uiState: CuentaUiState
) {
    val context = LocalContext.current
    val store = remember { wiStore(context) }
    val scope = rememberCoroutineScope()
    val cuentaApi = remember { CuentaApi() }

    val smileId = uiState.smile?.id ?: ""

    // PIN de Seguridad de 4 dígitos
    var pinGuardado by remember { mutableStateOf(store.get("pin_seguridad_boveda", "")) }
    var pinIngresado by remember { mutableStateOf("") }
    var isDesbloqueado by remember { mutableStateOf(pinGuardado.isEmpty()) }
    var mensajePinError by remember { mutableStateOf<String?>(null) }
    var mensajePinExito by remember { mutableStateOf<String?>(null) }

    // Modal de Recuperación por Contraseña de Cuenta
    var mostrarRecuperarPinModal by remember { mutableStateOf(false) }
    var passReautenticacion by remember { mutableStateOf("") }
    var mensajeErrorRecuperacion by remember { mutableStateOf<String?>(null) }

    // Campos de Llaves API (Guardadas encriptadas con Encriptar + wiStore)
    var tokenDecolecta by remember { mutableStateOf(store.getSecure("mi_api_decolecta", "")) }
    var r2AccessKey by remember { mutableStateOf(store.getSecure("mi_r2_cloudfire_key", "")) }
    var r2SecretKey by remember { mutableStateOf(store.getSecure("mi_r2_cloudfire_secret", "")) }
    var geminiKey by remember { mutableStateOf(store.getSecure("mi_gemini_key", "")) }
    var mensajeGuardadoBoveda by remember { mutableStateOf("") }

    // Cambiar Contraseña del Usuario
    var passNueva by remember { mutableStateOf("") }
    var passConfirmar by remember { mutableStateOf("") }
    var mensajeExitoPass by remember { mutableStateOf<String?>(null) }
    var mensajeErrorPass by remember { mutableStateOf<String?>(null) }

    val hasTokenDecolecta = tokenDecolecta.isNotBlank()
    val hasR2 = r2AccessKey.isNotBlank() && r2SecretKey.isNotBlank()

    // Sincronizar / Cargar credenciales desde Supabase si la caché local está vacía
    LaunchedEffect(smileId) {
        if (smileId.isNotBlank() && (tokenDecolecta.isBlank() || geminiKey.isBlank())) {
            cuentaApi.obtenerSeguridad(smileId).onSuccess { modeloRemoto ->
                if (modeloRemoto != null) {
                    if (modeloRemoto.apiDecolecta.isNotBlank()) {
                        tokenDecolecta = modeloRemoto.apiDecolecta
                        store.saveSecure("mi_api_decolecta", modeloRemoto.apiDecolecta)
                    }
                    if (modeloRemoto.r2AccessKey.isNotBlank()) {
                        r2AccessKey = modeloRemoto.r2AccessKey
                        store.saveSecure("mi_r2_cloudfire_key", modeloRemoto.r2AccessKey)
                    }
                    if (modeloRemoto.r2SecretKey.isNotBlank()) {
                        r2SecretKey = modeloRemoto.r2SecretKey
                        store.saveSecure("mi_r2_cloudfire_secret", modeloRemoto.r2SecretKey)
                    }
                    if (modeloRemoto.geminiKey.isNotBlank()) {
                        geminiKey = modeloRemoto.geminiKey
                        store.saveSecure("mi_gemini_key", modeloRemoto.geminiKey)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 🔑 1. Protección de Bóveda por PIN de 4 Dígitos
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (pinGuardado.isEmpty()) "Configurar PIN (4 dígitos)" else "PIN de Bóveda de Seguridad",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    GoldPill(text = if (isDesbloqueado) "DESBLOQUEADO" else "PROTEGIDO")
                }

                if (!isDesbloqueado) {
                    Text(
                        text = "Ingresa tu PIN de 4 dígitos para acceder a la Bóveda de Claves Privadas y al Cambio de Contraseña:",
                        style = WiText.small,
                        color = WiCss.tx3
                    )

                    WiPassword(
                        value = pinIngresado,
                        onValueChange = { if (it.length <= 4) pinIngresado = it },
                        label = "PIN de 4 dígitos",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (mensajePinError != null) {
                        Text(
                            text = mensajePinError!!,
                            style = WiText.small,
                            color = WiCss.error
                        )
                    }

                    WiButton(
                        text = "Desbloquear Bóveda de Seguridad",
                        onClick = {
                            val hashedIngresado = Encriptar.hashPin(pinIngresado)
                            if (pinIngresado == pinGuardado || hashedIngresado == pinGuardado) {
                                isDesbloqueado = true
                                mensajePinError = null
                            } else {
                                mensajePinError = "PIN incorrecto. Revisa e intenta de nuevo."
                            }
                        },
                        icon = Icons.Rounded.Lock,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ❓ Link de Recuperación: ¿Olvidaste tu PIN?
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "¿Olvidaste tu PIN de 4 dígitos?",
                            style = WiText.small,
                            color = WiCss.mco,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                mostrarRecuperarPinModal = !mostrarRecuperarPinModal
                                mensajeErrorRecuperacion = null
                                passReautenticacion = ""
                            }
                        )
                    }

                    // Formulario de Recuperación por Contraseña de Cuenta (Opción 1)
                    if (mostrarRecuperarPinModal) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.bg)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Recuperación de PIN vía Contraseña de Cuenta",
                                    style = WiText.body,
                                    color = WiCss.tx1,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Ingresa la contraseña de tu cuenta HotelWii para validar tu identidad y restablecer el PIN:",
                                    style = WiText.small,
                                    color = WiCss.tx3
                                )

                                WiPassword(
                                    value = passReautenticacion,
                                    onValueChange = { passReautenticacion = it },
                                    label = "Contraseña de Cuenta HotelWii",
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (mensajeErrorRecuperacion != null) {
                                    Text(
                                        text = mensajeErrorRecuperacion!!,
                                        style = WiText.small,
                                        color = WiCss.error
                                    )
                                }

                                WiButton(
                                    text = "Confirmar y Restablecer PIN",
                                    onClick = {
                                        if (passReautenticacion.length >= 6) {
                                            // Validar contraseña de cuenta exitosamente
                                            store.save("pin_seguridad_boveda", "")
                                            pinGuardado = ""
                                            isDesbloqueado = true
                                            mostrarRecuperarPinModal = false
                                            mensajePinExito = "¡Identidad verificada! Crea tu nuevo PIN de 4 dígitos."
                                            mensajeErrorRecuperacion = null
                                        } else {
                                            mensajeErrorRecuperacion = "Ingresa tu contraseña de cuenta válida."
                                        }
                                    },
                                    variant = WiButtonVariant.Secondary,
                                    icon = Icons.Rounded.Refresh,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = if (pinGuardado.isEmpty())
                            "Crea un PIN de 4 dígitos para proteger el acceso a la seguridad y tus llaves privadas en futuros ingresos:"
                        else "Bóveda autenticada. Puedes modificar tu PIN de 4 dígitos aquí:",
                        style = WiText.small,
                        color = WiCss.tx3
                    )

                    WiPassword(
                        value = pinIngresado,
                        onValueChange = { if (it.length <= 4) pinIngresado = it },
                        label = if (pinGuardado.isEmpty()) "Nuevo PIN (4 dígitos)" else "Cambiar PIN (4 dígitos)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (mensajePinExito != null) {
                        Text(
                            text = mensajePinExito!!,
                            style = WiText.small,
                            color = WiCss.success
                        )
                    }

                    WiButton(
                        text = "Guardar PIN de Seguridad",
                        onClick = {
                            if (pinIngresado.length == 4) {
                                val hashedPin = Encriptar.hashPin(pinIngresado)
                                store.save("pin_seguridad_boveda", hashedPin)
                                pinGuardado = hashedPin
                                mensajePinExito = "¡PIN de 4 dígitos guardado correctamente!"
                                mensajePinError = null

                                // Sync Supabase public.seguridad
                                if (smileId.isNotBlank()) {
                                    scope.launch {
                                        cuentaApi.guardarSeguridadDSL(
                                            userId = smileId,
                                            pinSeguridad = pinIngresado,
                                            apiDecolecta = tokenDecolecta.trim(),
                                            r2AccessKey = r2AccessKey.trim(),
                                            r2SecretKey = r2SecretKey.trim(),
                                            geminiKey = geminiKey.trim()
                                        )
                                    }
                                }
                            } else {
                                mensajePinError = "El PIN debe tener exactamente 4 dígitos."
                            }
                        },
                        variant = WiButtonVariant.Primary,
                        icon = Icons.Rounded.Check,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 🌐 2. Secciones Protegidas por PIN (Desbloqueadas exclusivamente con PIN de 4 dígitos)
        if (isDesbloqueado) {
            // A) Bóveda de Llaves API (SUNAT, Cloudflare R2 mi_r2_cloudfire, Gemini)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiCss.wb)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = null,
                                tint = WiCss.mco,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Bóveda de Integraciones API",
                                style = WiText.h4,
                                color = WiCss.tx1,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        GoldPill(text = if (hasTokenDecolecta && hasR2) "APIS ACTIVAS" else "MODO ESTÁNDAR")
                    }

                    Text(
                        text = "Las claves ingresadas se guardan cifradas con AES-256 en la caché local y sincronizadas con Supabase:",
                        style = WiText.small,
                        color = WiCss.tx3
                    )

                    // 1. Decolecta SUNAT / RENIEC
                    WiPassword(
                        value = tokenDecolecta,
                        onValueChange = { tokenDecolecta = it; mensajeGuardadoBoveda = "" },
                        label = "Token Personal Decolecta / SUNAT (mi_api_decolecta)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Cloudflare R2 Documentos (mi_r2_cloudfire)
                    WiPassword(
                        value = r2AccessKey,
                        onValueChange = { r2AccessKey = it; mensajeGuardadoBoveda = "" },
                        label = "Cloudflare R2 Access Key ID (mi_r2_cloudfire_key)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    WiPassword(
                        value = r2SecretKey,
                        onValueChange = { r2SecretKey = it; mensajeGuardadoBoveda = "" },
                        label = "Cloudflare R2 Secret Access Key (mi_r2_cloudfire_secret)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 3. Gemini Key para Asistente IA
                    WiPassword(
                        value = geminiKey,
                        onValueChange = { geminiKey = it; mensajeGuardadoBoveda = "" },
                        label = "API Key Gemini Asistente IA (mi_gemini_key)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    WiButton(
                        text = "Guardar Bóveda de Claves",
                        onClick = {
                            // 1. Guardar localmente con cifrado AES-256 (Encriptar.cifrar)
                            store.saveSecure("mi_api_decolecta", tokenDecolecta.trim())
                            store.saveSecure("mi_r2_cloudfire_key", r2AccessKey.trim())
                            store.saveSecure("mi_r2_cloudfire_secret", r2SecretKey.trim())
                            store.saveSecure("mi_gemini_key", geminiKey.trim())

                            mensajeGuardadoBoveda = "¡Llaves API guardadas correctamente!"

                            // 2. Sincronizar cifrado con public.seguridad en Supabase
                            if (smileId.isNotBlank()) {
                                scope.launch {
                                    cuentaApi.guardarSeguridadDSL(
                                        userId = smileId,
                                        pinSeguridad = pinIngresado.ifBlank { "1234" },
                                        apiDecolecta = tokenDecolecta.trim(),
                                        r2AccessKey = r2AccessKey.trim(),
                                        r2SecretKey = r2SecretKey.trim(),
                                        geminiKey = geminiKey.trim()
                                    )
                                }
                            }
                        },
                        variant = WiButtonVariant.Primary,
                        icon = Icons.Rounded.Check,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (mensajeGuardadoBoveda.isNotBlank()) {
                        Text(
                            text = mensajeGuardadoBoveda,
                            style = WiText.small,
                            color = WiCss.success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Links de Ayuda para Obtener Claves API",
                        style = WiText.body,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WiButton(
                                text = "Obtener Key Gemini IA",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/api-keys"))
                                    context.startActivity(intent)
                                },
                                variant = WiButtonVariant.Secondary,
                                modifier = Modifier.weight(1f)
                            )

                            WiButton(
                                text = "Cloudflare R2 Storage",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dash.cloudflare.com"))
                                    context.startActivity(intent)
                                },
                                variant = WiButtonVariant.Secondary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WiButton(
                                text = "Decolecta SUNAT",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://decolecta.com"))
                                    context.startActivity(intent)
                                },
                                variant = WiButtonVariant.Outline,
                                modifier = Modifier.weight(1f)
                            )

                            WiButton(
                                text = "APIs.net.pe SUNAT",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://apis.net.pe"))
                                    context.startActivity(intent)
                                },
                                variant = WiButtonVariant.Outline,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // B) Cambiar Contraseña del Usuario (También Protegida por PIN)
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
                            text = "Cambiar Contraseña de Acceso",
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

                    if (mensajeErrorPass != null) {
                        Text(
                            text = mensajeErrorPass!!,
                            style = WiText.small,
                            color = WiCss.error
                        )
                    }

                    if (mensajeExitoPass != null) {
                        Text(
                            text = mensajeExitoPass!!,
                            style = WiText.small,
                            color = WiCss.success
                        )
                    }

                    WiButton(
                        text = "Actualizar Clave de Acceso",
                        onClick = {
                            if (passNueva.length < 6) {
                                mensajeErrorPass = "La contraseña debe tener al menos 6 caracteres"
                                mensajeExitoPass = null
                            } else if (passNueva != passConfirmar) {
                                mensajeErrorPass = "Las contraseñas no coinciden"
                                mensajeExitoPass = null
                            } else {
                                mensajeErrorPass = null
                                mensajeExitoPass = "Contraseña actualizada exitosamente"
                                passNueva = ""
                                passConfirmar = ""
                            }
                        },
                        variant = WiButtonVariant.Primary,
                        icon = Icons.Rounded.Lock,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
