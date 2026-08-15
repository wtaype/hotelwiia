package com.hotelwii.feature.actualizar.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.actualizar.ActualizarUiState

/**
 * 📡 TarjetaHeroRadar — Card central con radar reactivo de estado y micro-animación pulsante
 */
@Composable
fun TarjetaHeroRadar(
    uiState: ActualizarUiState,
    versionInstalada: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    // Configuración visual según el estado
    val (colorPrincipal, iconoCentral, tituloEstado, subtituloEstado) = when (uiState) {
        is ActualizarUiState.Inactivo -> Quadruple(
            WiCss.mco,
            WiIcons.Refresh,
            "HotelWii v$versionInstalada",
            "Toca el botón central para comprobar novedades en Cloudflare R2"
        )
        is ActualizarUiState.Comprobando -> Quadruple(
            WiCss.mco,
            Icons.Rounded.Refresh,
            "Verificando en la Nube...",
            "Consultando servidor seguro en tiempo real"
        )
        is ActualizarUiState.AlDia -> Quadruple(
            WiCss.success,
            Icons.Rounded.CheckCircle,
            "¡HotelWii está al día!",
            "Tienes instalada la versión más reciente (v${uiState.versionActual})"
        )
        is ActualizarUiState.ActualizacionDisponible -> Quadruple(
            WiCss.warning,
            WiIcons.CloudUpload,
            "Nueva Versión v${uiState.info.versionName}",
            uiState.info.releaseNotes.ifBlank { "Nuevas mejoras de velocidad y optimizaciones listas." }
        )
        is ActualizarUiState.Descargando -> Quadruple(
            WiCss.mco,
            WiIcons.CloudUpload,
            "Descargando Actualización...",
            "Descarga fluida desde Cloudflare R2 (${uiState.mbDescargados})"
        )
        is ActualizarUiState.ListoParaInstalar -> Quadruple(
            WiCss.success,
            Icons.Rounded.CheckCircle,
            "Descarga Lista (v${uiState.versionName})",
            "Presiona 'Instalar Ahora' para completar la actualización"
        )
        is ActualizarUiState.Error -> Quadruple(
            WiCss.error,
            Icons.Rounded.Warning,
            "Aviso de Conexión",
            uiState.mensaje
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WiCss.wb,
                        colorPrincipal.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = colorPrincipal.copy(alpha = 0.25f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Radar animado
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(96.dp)
            ) {
                // Onda expansiva de pulso
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (uiState is ActualizarUiState.Comprobando || uiState is ActualizarUiState.Descargando) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(colorPrincipal.copy(alpha = if (uiState is ActualizarUiState.Comprobando || uiState is ActualizarUiState.Descargando) pulseAlpha else 0.12f))
                )

                // Núcleo del Radar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(colorPrincipal.copy(alpha = 0.18f))
                        .border(1.5.dp, colorPrincipal.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconoCentral,
                        contentDescription = null,
                        tint = colorPrincipal,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Textos de estado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = tituloEstado,
                    style = WiText.h3,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtituloEstado,
                    style = WiText.body,
                    color = WiCss.tx2,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Tag píldora de versión actual
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(WiCss.inp)
                    .border(1.dp, WiCss.brd.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(colorPrincipal)
                    )
                    Text(
                        text = "Instalada: v$versionInstalada",
                        style = WiText.small,
                        color = WiCss.tx2,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
