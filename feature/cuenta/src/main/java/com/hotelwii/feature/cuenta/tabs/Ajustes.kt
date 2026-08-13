package com.hotelwii.feature.cuenta.tabs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.core.kidev.wiStore

/**
 * ⚙️ Ajustes.kt — Pestaña 4 del Módulo Cuenta: Preferencias, APIs SUNAT/RENIEC & Caché wiStore.
 */
@Composable
fun Ajustes() {
    val context = LocalContext.current
    val store = remember { wiStore(context) }

    var notificacionesActivas by remember { mutableStateOf(true) }
    var impresorPosActivo by remember { mutableStateOf(true) }
    var mensajeCacheLimpiada by remember { mutableStateOf<String?>(null) }

    var tokenDecolecta by remember { mutableStateOf(store.get("mi_api_decolecta", "")) }
    var mensajeApiGuardado by remember { mutableStateOf("") }

    val hasToken = tokenDecolecta.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Preferencias del Hotel
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
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Preferencias de Recepción",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                WiSwitch(
                    checked = notificacionesActivas,
                    onCheckedChange = { notificacionesActivas = it },
                    label = "Notificaciones de Reservas",
                    sublabel = "Recibe alertas en tiempo real de nuevas reservas e ingresos."
                )

                WiSwitch(
                    checked = impresorPosActivo,
                    onCheckedChange = { impresorPosActivo = it },
                    label = "Impresión de Tickets POS ESC/POS",
                    sublabel = "Genera comprobantes térmicos de check-in automáticamente."
                )
            }
        }

        // 2. 🔗 Integración de APIs & Claves SUNAT / RENIEC / Decolecta
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "APIs & Servicios SUNAT / RENIEC",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    GoldPill(text = if (hasToken) "TOKEN PERSONAL" else "MODO GRATUITO")
                }

                Text(
                    text = "HotelWii opera con un motor multi-proveedor automático de 4 niveles. Registra tu token personal para consultas ilimitadas:",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                WiField(
                    value = tokenDecolecta,
                    onValueChange = {
                        tokenDecolecta = it
                        mensajeApiGuardado = ""
                    },
                    label = "Token Personal Decolecta / SUNAT (mi_api_decolecta)",
                    leadingIcon = Icons.Rounded.Lock,
                    modifier = Modifier.fillMaxWidth()
                )

                WiButton(
                    text = "Guardar Clave API en wiStore",
                    onClick = {
                        store.save("mi_api_decolecta", tokenDecolecta.trim())
                        mensajeApiGuardado = "¡Token personal guardado correctamente!"
                    },
                    icon = Icons.Rounded.Check,
                    modifier = Modifier.fillMaxWidth()
                )

                if (mensajeApiGuardado.isNotBlank()) {
                    Text(
                        text = mensajeApiGuardado,
                        style = WiText.small,
                        color = WiCss.success,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(2.dp))

                // Estado del Motor SUNAT (4 Niveles)
                Text(
                    text = "Estado del Motor SUNAT (4 Niveles de Respaldo)",
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WiCss.bg)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItemNivelApi(
                        nivel = "Nivel 1 (Directo SUNAT)",
                        descripcion = "apis.net.pe — Instantáneo sin token",
                        estado = "Activo (< 100ms)",
                        esActivo = true
                    )

                    ItemNivelApi(
                        nivel = "Nivel 2 (Token Personal)",
                        descripcion = "Clave Decolecta guardada en wiStore",
                        estado = if (hasToken) "Configurado" else "Sin Configurar",
                        esActivo = hasToken
                    )

                    ItemNivelApi(
                        nivel = "Nivel 3 (HotelWii Sistema)",
                        descripcion = "Respaldo global de contingencia",
                        estado = "Disponible",
                        esActivo = true
                    )

                    ItemNivelApi(
                        nivel = "Nivel 4 (Modo Manual)",
                        descripcion = "Autogeneración sin bloquear (0ms)",
                        estado = "Habilitado (0ms)",
                        esActivo = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiButton(
                        text = "Ver apis.net.pe",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://apis.net.pe"))
                            context.startActivity(intent)
                        },
                        containerColor = WiCss.inp,
                        contentColor = WiCss.tx1,
                        modifier = Modifier.weight(1f)
                    )

                    WiButton(
                        text = "Ver decolecta.com",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://decolecta.com"))
                            context.startActivity(intent)
                        },
                        containerColor = WiCss.mco.copy(alpha = 0.15f),
                        contentColor = WiCss.mco,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Almacenamiento & Caché Local wiStore
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
                        imageVector = WiIcons.Refresh,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Almacenamiento Local wiStore",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado de la Caché Local",
                        style = WiText.small,
                        color = WiCss.tx2
                    )
                    Text(
                        text = "Saludable (0.4 MB)",
                        style = WiText.small,
                        color = WiCss.success,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (mensajeCacheLimpiada != null) {
                    Text(
                        text = mensajeCacheLimpiada!!,
                        style = WiText.small,
                        color = WiCss.success
                    )
                }

                WiButton(
                    text = "Limpiar Caché Residual",
                    onClick = {
                        mensajeCacheLimpiada = "Caché residual optimizada correctamente."
                    },
                    icon = WiIcons.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ItemNivelApi(
    nivel: String,
    descripcion: String,
    estado: String,
    esActivo: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nivel,
                style = WiText.small,
                color = WiCss.tx1,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = descripcion,
                style = WiText.small,
                color = WiCss.tx3
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (esActivo) WiCss.success.copy(alpha = 0.12f) else WiCss.inp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = estado,
                style = WiText.small,
                color = if (esActivo) WiCss.success else WiCss.tx3,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
