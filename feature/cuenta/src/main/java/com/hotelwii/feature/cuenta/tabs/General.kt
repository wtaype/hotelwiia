package com.hotelwii.feature.cuenta.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.FzSmart
import com.hotelwii.core.kicss.HotelWiTemas
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiDialog
import com.hotelwii.feature.cuenta.CuentaUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * 📱 General.kt — Pestaña 1: Resumen de Perfil VIP, Selector de Temas Oficiales, Tamaño de Fuente Smart & Cierre de Sesión.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun General(
    uiState: CuentaUiState,
    onSeleccionarTema: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val smile = uiState.smile
    val nombreCompleto = "${smile?.nombre ?: ""} ${smile?.apellidos ?: ""}".ifBlank { "Usuario HotelWii" }
    val usuarioTag = "@${smile?.usuario ?: "recepcion"}"
    val segmentoTag = (smile?.segmento ?: "NEGOCIOS").uppercase()
    val avatarUrl = smile?.avatar

    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    val bitmapState = produceState<ImageBitmap?>(initialValue = null, key1 = avatarUrl ?: "") {
        if (!avatarUrl.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val stream = URL(avatarUrl).openStream()
                    val bmp = android.graphics.BitmapFactory.decodeStream(stream)
                    value = bmp?.asImageBitmap()
                } catch (e: Exception) {
                    value = null
                }
            }
        } else {
            value = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Card Resumen de Perfil General
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar Circle HD o Fallback logo_circle
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(WiCss.mco.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    val loadedBitmap = bitmapState.value
                    if (loadedBitmap != null) {
                        Image(
                            bitmap = loadedBitmap,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = com.hotelwii.core.wii.R.drawable.logo_circle),
                            contentDescription = "Avatar",
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = nombreCompleto,
                            style = WiText.h3,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                        GoldPill(text = segmentoTag)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = usuarioTag,
                        style = WiText.body,
                        color = WiCss.mco,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = smile?.email ?: "usuario@hotelwii.com",
                        style = WiText.small,
                        color = WiCss.tx3
                    )
                }
            }
        }

        // 2. Card Selección de Temas Oficiales con Muestra de Color
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
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Tema de la Aplicación",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Selecciona tu tema favorito para personalizar la apariencia de HotelWii.",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                Spacer(Modifier.height(4.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HotelWiTemas.forEach { temaItem ->
                        val isSelected = uiState.temaActivo.equals(temaItem.name, ignoreCase = true)
                        val borderColor = if (isSelected) WiCss.mco else WiCss.brd.copy(alpha = 0.5f)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(temaItem.wb)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onSeleccionarTema(temaItem.name) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(temaItem.mco)
                                )
                                Text(
                                    text = temaItem.name,
                                    style = WiText.body,
                                    color = temaItem.tx,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Activo",
                                        tint = WiCss.mco,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Card Tamaño de Fuente Smart (FzSmart)
        var fontScale by remember { mutableStateOf(FzSmart.scaleFactor) }
        val pctText = "${(fontScale * 100).toInt()}%"
        val labelPreset = when {
            fontScale <= 0.88f -> "Pequeño"
            fontScale <= 1.05f -> "Normal"
            fontScale <= 1.20f -> "Grande"
            else -> "Extra"
        }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Tamaño de Fuente Smart",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Botón de Reset de Tamaño de Fuente
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(WiCss.mco.copy(alpha = 0.15f))
                                .clickable {
                                    fontScale = 1.0f
                                    FzSmart.scaleFactor = 1.0f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Restablecer Fuente",
                                tint = WiCss.mco,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        GoldPill(text = "$pctText ($labelPreset)")
                    }
                }

                Text(
                    text = "Ajusta la escala tipográfica de toda la aplicación en tiempo real.",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                Slider(
                    value = fontScale,
                    onValueChange = { nuevaEscala ->
                        fontScale = nuevaEscala
                        FzSmart.scaleFactor = nuevaEscala
                    },
                    valueRange = 0.85f..1.30f,
                    colors = SliderDefaults.colors(
                        thumbColor = WiCss.mco,
                        activeTrackColor = WiCss.mco,
                        inactiveTrackColor = WiCss.brd
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Presets Rápidos
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "Pequeño" to 0.85f,
                        "Normal" to 1.00f,
                        "Grande" to 1.15f,
                        "Extra" to 1.30f
                    )
                    presets.forEach { (nombre, valEscala) ->
                        val isPresetSelected = kotlin.math.abs(fontScale - valEscala) < 0.05f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(
                                    if (isPresetSelected) WiCss.mco.copy(alpha = 0.15f)
                                    else WiCss.inp
                                )
                                .border(
                                    width = if (isPresetSelected) 1.5.dp else 1.dp,
                                    color = if (isPresetSelected) WiCss.mco else WiCss.brd,
                                    shape = RoundedCornerShape(999.dp)
                                )
                                .clickable {
                                    fontScale = valEscala
                                    FzSmart.scaleFactor = valEscala
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = nombre,
                                style = WiText.small,
                                color = if (isPresetSelected) WiCss.mco else WiCss.tx2,
                                fontWeight = if (isPresetSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 4. Bloque de Seguridad / Cerrar Sesión
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Sesión Activa",
                    style = WiText.h4,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Al cerrar sesión deberás volver a ingresar tus credenciales para acceder a la recepción.",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                WiButton(
                    text = "Cerrar Sesión",
                    onClick = { mostrarDialogoCerrarSesion = true },
                    containerColor = WiCss.error,
                    icon = Icons.Rounded.Lock,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    WiDialog(
        show = mostrarDialogoCerrarSesion,
        title = "Cerrar Sesión",
        text = "¿Estás seguro de que deseas salir de HotelWii?",
        confirmText = "Sí, Cerrar Sesión",
        dismissText = "Cancelar",
        onConfirm = {
            mostrarDialogoCerrarSesion = false
            onCerrarSesion()
        },
        onDismiss = { mostrarDialogoCerrarSesion = false }
    )
}
