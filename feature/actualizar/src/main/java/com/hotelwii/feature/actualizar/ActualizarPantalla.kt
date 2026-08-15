package com.hotelwii.feature.actualizar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.actualizar.components.ProgresoStreamCard
import com.hotelwii.feature.actualizar.components.TarjetaHeroRadar

/**
 * 🚀 ActualizarPantalla — Centro Inteligente de Actualizaciones OTA (0 Tabs, 1-Click Silencioso)
 */
@Composable
fun ActualizarPantalla(
    viewModel: ActualizarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Card Central con Radar Reactivo
        TarjetaHeroRadar(
            uiState = uiState,
            versionInstalada = viewModel.versionInstalada
        )

        // 2. Barra de Progreso en Vivo (Solo activa durante la descarga)
        if (uiState is ActualizarUiState.Descargando) {
            val descargandoState = uiState as ActualizarUiState.Descargando
            ProgresoStreamCard(
                progreso = descargandoState.progreso,
                mbDescargados = descargandoState.mbDescargados
            )
        }

        // 3. Botón de Acción Inteligente Central
        when (val state = uiState) {
            is ActualizarUiState.Inactivo, is ActualizarUiState.AlDia -> {
                WiButton(
                    text = "Buscar Actualizaciones",
                    onClick = { viewModel.verificarNovedades() },
                    icon = WiIcons.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ActualizarUiState.Comprobando -> {
                WiButton(
                    text = "Consultando Nube...",
                    onClick = {},
                    loading = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ActualizarUiState.ActualizacionDisponible -> {
                WiButton(
                    text = "Descargar e Instalar v${state.info.versionName}",
                    onClick = { viewModel.descargarEInstalar(state.info) },
                    icon = WiIcons.CloudUpload,
                    containerColor = WiCss.success,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ActualizarUiState.Descargando -> {
                val porcentaje = (state.progreso * 100).toInt()
                WiButton(
                    text = "Descargando ($porcentaje%)...",
                    onClick = {},
                    loading = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ActualizarUiState.ListoParaInstalar -> {
                WiButton(
                    text = "Abrir Instalador de HotelWii",
                    onClick = { viewModel.ejecutarInstalador(state.apkFile) },
                    icon = Icons.Rounded.CheckCircle,
                    containerColor = WiCss.success,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is ActualizarUiState.Error -> {
                WiButton(
                    text = "Reintentar Conexión",
                    onClick = { viewModel.verificarNovedades() },
                    icon = Icons.Rounded.Refresh,
                    variant = WiButtonVariant.Error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. Tarjeta Informativa de Seguridad & Cloudflare R2
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Infraestructura & Seguridad OTA",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                InfoPunto(
                    icono = WiIcons.Building,
                    titulo = "Distribución Cloudflare R2",
                    descripcion = "Descargas ultra veloces servidas desde nodos CDN peruanos de baja latencia."
                )

                InfoPunto(
                    icono = Icons.Rounded.Lock,
                    titulo = "Protección de Datos & Sesiones",
                    descripcion = "Tus reservas, ventas y sesiones de hotel se preservan intactas al actualizar."
                )

                InfoPunto(
                    icono = Icons.Rounded.CheckCircle,
                    titulo = "Cero Fricción (0 Popups)",
                    descripcion = "Actualización silenciosa y bajo demanda sin interrupciones durante el turno de recepción."
                )
            }
        }
    }
}

@Composable
private fun InfoPunto(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = WiCss.mco.copy(alpha = 0.85f),
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = titulo,
                style = WiText.body,
                color = WiCss.tx1,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = descripcion,
                style = WiText.small,
                color = WiCss.tx3,
                fontSize = 11.5.sp
            )
        }
    }
}
