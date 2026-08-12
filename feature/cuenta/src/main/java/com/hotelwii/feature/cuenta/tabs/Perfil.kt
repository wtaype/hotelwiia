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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiField
import com.hotelwii.feature.cuenta.CuentaUiState
import com.hotelwii.feature.cuenta.CuentaViewModel

/**
 * ✏️ Perfil.kt — Pestaña 2: Edición Completa de Datos Personales del Usuario.
 */
@Composable
fun Perfil(
    uiState: CuentaUiState,
    viewModel: CuentaViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de éxito de guardado
        if (!uiState.mensajeExito.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WiCss.success.copy(alpha = 0.15f))
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.mensajeExito,
                    style = WiText.body,
                    color = WiCss.success,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 1. Datos Personales Editables
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
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Datos Personales",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                WiField(
                    value = uiState.nombreEdit,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = "Nombre",
                    leadingIcon = Icons.Rounded.Person
                )

                WiField(
                    value = uiState.apellidosEdit,
                    onValueChange = { viewModel.onApellidosChange(it) },
                    label = "Apellidos",
                    leadingIcon = Icons.Rounded.Person
                )

                WiField(
                    value = uiState.usuarioEdit,
                    onValueChange = { viewModel.onUsuarioChange(it) },
                    label = "Nombre de Usuario (@usuario)",
                    leadingIcon = Icons.Rounded.Person
                )

                WiField(
                    value = uiState.emailEdit,
                    onValueChange = { },
                    label = "Correo Electrónico (Solo lectura)",
                    leadingIcon = Icons.Rounded.Lock,
                    singleLine = true
                )

                WiField(
                    value = uiState.bioEdit,
                    onValueChange = { viewModel.onBioChange(it) },
                    label = "Biografía / Descripción del hotel",
                    singleLine = false,
                    maxLines = 3
                )

                WiField(
                    value = uiState.segmentoEdit,
                    onValueChange = { viewModel.onSegmentoChange(it) },
                    label = "Segmento (ej: hotel, hospedaje, resort)"
                )

                WiButton(
                    text = "Guardar Cambios",
                    onClick = { viewModel.guardarPerfil() },
                    icon = Icons.Rounded.Edit,
                    loading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 2. Plan de Suscripción & Mapeo VIP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Plan de Suscripción",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    GoldPill(text = (uiState.smile?.plan ?: "FREE").uppercase())
                }

                Text(
                    text = "Actualmente tu cuenta dispone del plan gratuito de prueba HotelWii.",
                    style = WiText.small,
                    color = WiCss.tx3
                )

                WiButton(
                    text = "Gestionar Suscripción",
                    onClick = {
                        viewModel.abrirModalFuturo(
                            titulo = "Planes & Suscripciones",
                            mensaje = "Esta funcionalidad se implementará a futuro. Estamos trabajando para traerte el módulo de suscripciones y facturación hotelera."
                        )
                    },
                    containerColor = WiCss.mco.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
