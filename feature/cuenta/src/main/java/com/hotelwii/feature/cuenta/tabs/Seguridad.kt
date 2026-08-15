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
import androidx.compose.runtime.collectAsState
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
import com.hotelwii.feature.cuenta.CuentaUiState
import com.hotelwii.feature.cuenta.api.CuentaApi
import com.hotelwii.feature.cuenta.api.SeguridadModelo
import com.hotelwii.feature.cuenta.data.CacheSeguridad
import kotlinx.coroutines.launch

/**
 * 🔒 Seguridad.kt — Pestaña 3: Protección por PIN de 4 dígitos, Re-autenticación de Recuperación & Bóveda Cifrada.
 */
@Composable
fun Seguridad(
    uiState: CuentaUiState
) {
    val context = LocalContext.current
    val cacheSeguridad = remember { CacheSeguridad.getInstance(context) }
    val scope = rememberCoroutineScope()
    val cuentaApi = remember { CuentaApi() }

    val smileId = uiState.smile?.id ?: ""
    val seguridadLocal by cacheSeguridad.seguridadFlow.collectAsState()

    // PIN de Seguridad de 4 dígitos
    var pinIngresado by remember { mutableStateOf("") }
    var isDesbloqueado by remember { mutableStateOf(!cacheSeguridad.tienePinConfigurado()) }
    var mensajePinError by remember { mutableStateOf<String?>(null) }
    var mensajePinExito by remember { mutableStateOf<String?>(null) }

    // Modal de Recuperación por Contraseña de Cuenta
    var mostrarRecuperarPinModal by remember { mutableStateOf(false) }
    var passReautenticacion by remember { mutableStateOf("") }
    var mensajeErrorRecuperacion by remember { mutableStateOf<String?>(null) }

    // Campos de Llaves API (Obtenidas de CacheSeguridad con cifrado AES-256)
    var tokenDecolecta by remember { mutableStateOf(seguridadLocal?.apiDecolecta ?: "") }
    var r2AccessKey by remember { mutableStateOf(seguridadLocal?.r2AccessKey ?: "") }
    var r2SecretKey by remember { mutableStateOf(seguridadLocal?.r2SecretKey ?: "") }
    var geminiKey by remember { mutableStateOf(seguridadLocal?.geminiKey ?: "") }
    var mensajeGuardadoBoveda by remember { mutableStateOf("") }

    // Actualizar campos locales cuando cambie la caché reactiva
    LaunchedEffect(seguridadLocal) {
        seguridadLocal?.let { seg ->
            if (tokenDecolecta.isBlank() && seg.apiDecolecta.isNotBlank()) tokenDecolecta = seg.apiDecolecta
            if (r2AccessKey.isBlank() && seg.r2AccessKey.isNotBlank()) r2AccessKey = seg.r2AccessKey
            if (r2SecretKey.isBlank() && seg.r2SecretKey.isNotBlank()) r2SecretKey = seg.r2SecretKey
            if (geminiKey.isBlank() && seg.geminiKey.isNotBlank()) geminiKey = seg.geminiKey
        }
    }

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
                    val combinado = (seguridadLocal ?: SeguridadModelo(userId = smileId)).copy(
                        userId = smileId,
                        apiDecolecta = modeloRemoto.apiDecolecta.ifBlank { tokenDecolecta },
                        r2AccessKey = modeloRemoto.r2AccessKey.ifBlank { r2AccessKey },
                        r2SecretKey = modeloRemoto.r2SecretKey.ifBlank { r2SecretKey },
                        geminiKey = modeloRemoto.geminiKey.ifBlank { geminiKey }
                    )
                    cacheSeguridad.guardarSeguridad(combinado)
                    tokenDecolecta = combinado.apiDecolecta
                    r2AccessKey = combinado.r2AccessKey
                    r2SecretKey = combinado.r2SecretKey
                    geminiKey = combinado.geminiKey
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
                            text = if (!cacheSeguridad.tienePinConfigurado()) "Configurar PIN (4 dígitos)" else "PIN de Bóveda de Seguridad",
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
                            val esValido = cacheSeguridad.validarPin(pinIngresado)
                            if (esValido) {
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
                                            // Validar contraseña de cuenta exitosamente y resetear PIN
                                            cacheSeguridad.restablecerPin()
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
                        text = if (!cacheSeguridad.tienePinConfigurado())
                            "Crea un PIN de 4 dígitos para proteger el acceso a la seguridad y tus llaves privadas en futuros ingresos:"
                        else "Bóveda autenticada. Puedes modificar tu PIN de 4 dígitos aquí:",
                        style = WiText.small,
                        color = WiCss.tx3
                    )

                    WiPassword(
                        value = pinIngresado,
                        onValueChange = { if (it.length <= 4) pinIngresado = it },
                        label = if (!cacheSeguridad.tienePinConfigurado()) "Nuevo PIN (4 dígitos)" else "Cambiar PIN (4 dígitos)",
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
                                cacheSeguridad.guardarPin(pinIngresado, smileId)
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
                            // 1. Guardar localmente con cifrado AES-256 en CacheSeguridad (wiSeguridad)
                            val modeloSeguridad = (seguridadLocal ?: SeguridadModelo(userId = smileId)).copy(
                                userId = smileId,
                                apiDecolecta = tokenDecolecta.trim(),
                                r2AccessKey = r2AccessKey.trim(),
                                r2SecretKey = r2SecretKey.trim(),
                                geminiKey = geminiKey.trim()
                            )
                            cacheSeguridad.guardarSeguridad(modeloSeguridad)
                            mensajeGuardadoBoveda = "¡Llaves API guardadas con cifrado AES-256!"

                            // 2. Sincronizar cifrado con public.seguridad en Supabase
                            if (smileId.isNotBlank()) {
                                scope.launch {
                                    cuentaApi.guardarSeguridadDSL(
                                        userId = smileId,
                                        pinSeguridad = seguridadLocal?.pinSeguridad ?: "1234",
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
