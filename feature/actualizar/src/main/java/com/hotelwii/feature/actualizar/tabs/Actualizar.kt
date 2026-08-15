package com.hotelwii.feature.actualizar.tabs

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.feature.actualizar.WiVersionInfo
import java.io.File

/**
 * Actualizar — Vista central con Hero Radar, Notas y Descarga en 1 Clic
 */
@Composable
fun Actualizar(
    versionInstalada: String,
    infoRemota: WiVersionInfo?,
    isComprobando: Boolean,
    isDescargando: Boolean,
    progresoDescarga: Float,
    textoDescarga: String,
    apkDescargado: File?,
    mensajeError: String?,
    onComprobar: () -> Unit,
    onDescargar: (WiVersionInfo) -> Unit,
    onInstalar: (File) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TARJETA HERO RADAR
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb),
            border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Radar / Check animado con vector icons
                RadarHeroIcon(
                    isComprobando = isComprobando,
                    isDescargando = isDescargando,
                    hayActualizacion = infoRemota != null,
                    isListo = apkDescargado != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                val titulo = when {
                    isComprobando -> "Buscando Actualización..."
                    isDescargando -> "Descargando Actualización"
                    apkDescargado != null -> "Descarga Lista (${infoRemota?.versionName ?: ""})"
                    infoRemota != null -> "Nueva Versión Disponible"
                    else -> "HotelWii está al Día"
                }

                val subtitulo = when {
                    isComprobando -> "Conectando con la red oficial de Cloudflare R2"
                    isDescargando -> textoDescarga
                    apkDescargado != null -> "Presiona 'Instalar Ahora' para completar la actualización"
                    infoRemota != null -> "Versión ${infoRemota.versionName} lista para descargar"
                    else -> "Tienes la última versión oficial instalada en tu dispositivo"
                }

                Text(
                    text = titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WiCss.tx,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitulo,
                    fontSize = 13.sp,
                    color = WiCss.tx3,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Píldora de versión actual
                Box(
                    modifier = Modifier
                        .background(WiCss.brd.copy(alpha = 0.4f), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(WiCss.success, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instalada: v$versionInstalada",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = WiCss.tx
                        )
                    }
                }

                // Barra de progreso si está descargando
                if (isDescargando) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { progresoDescarga },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = WiCss.bt,
                        trackColor = WiCss.brd,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // NOTAS DE VERSIÓN SI EXISTE ACTUALIZACIÓN
        if (infoRemota != null && infoRemota.releaseNotes.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WiCss.wb.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = WiIcons.Info,
                            contentDescription = "Novedades",
                            tint = WiCss.bt,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Novedades de la Versión ${infoRemota.versionName}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiCss.tx
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = infoRemota.releaseNotes,
                        fontSize = 13.sp,
                        color = WiCss.tx1,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ALERTA DE ERROR SI HUBIERA
        if (mensajeError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WiCss.error.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, WiCss.error.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = WiIcons.Info,
                        contentDescription = "Error",
                        tint = WiCss.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mensajeError,
                        color = WiCss.error,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // BOTÓN PRINCIPAL DE ACCIÓN (1 CLIC)
        val buttonColor = if (apkDescargado != null || infoRemota != null) WiCss.success else WiCss.bt

        Button(
            onClick = {
                when {
                    apkDescargado != null -> onInstalar(apkDescargado)
                    infoRemota != null -> onDescargar(infoRemota)
                    else -> onComprobar()
                }
            },
            enabled = !isComprobando && !isDescargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            )
        ) {
            if (isComprobando || isDescargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                val iconVector = when {
                    apkDescargado != null -> WiIcons.Check
                    infoRemota != null -> WiIcons.SystemUpdate
                    else -> WiIcons.Refresh
                }
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            val labelBoton = when {
                isComprobando -> "Comprobando..."
                isDescargando -> "Descargando..."
                apkDescargado != null -> "Abrir Instalador de HotelWii"
                infoRemota != null -> "Descargar e Instalar (v${infoRemota.versionName})"
                else -> "Comprobar Actualización"
            }

            Text(
                text = labelBoton,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // CARD DE INFRAESTRUCTURA & SEGURIDAD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb.copy(alpha = 0.7f)),
            border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = WiIcons.Security,
                        contentDescription = null,
                        tint = WiCss.bt,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Infraestructura & Seguridad OTA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiCss.tx
                    )
                }

                ItemInfra(
                    icono = WiIcons.CloudUpload,
                    titulo = "Distribución Cloudflare R2",
                    desc = "Descargas ultra veloces servidas desde nodos CDN de baja latencia."
                )

                ItemInfra(
                    icono = WiIcons.Security,
                    titulo = "Protección de Datos & Sesiones",
                    desc = "Tus reservas, habitaciones y cobros se preservan intactos al actualizar."
                )

                ItemInfra(
                    icono = WiIcons.Bolt,
                    titulo = "Cero Fricción",
                    desc = "Actualización fluida y bajo demanda sin interrumpir el turno de recepción."
                )
            }
        }
    }
}

@Composable
private fun RadarHeroIcon(
    isComprobando: Boolean,
    isDescargando: Boolean,
    hayActualizacion: Boolean,
    isListo: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isComprobando || isDescargando) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarScale"
    )

    val colorHero = when {
        isListo -> WiCss.success
        hayActualizacion -> WiCss.bt
        isDescargando -> WiCss.bt
        isComprobando -> WiCss.bt
        else -> WiCss.success
    }

    Box(
        modifier = Modifier
            .size(90.dp)
            .scale(if (isComprobando || isDescargando) scale else 1f)
            .background(colorHero.copy(alpha = 0.12f), CircleShape)
            .border(2.dp, colorHero.copy(alpha = 0.35f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(colorHero.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val iconVector = when {
                isListo -> WiIcons.Check
                isDescargando -> WiIcons.SystemUpdate
                hayActualizacion -> WiIcons.SystemUpdate
                isComprobando -> WiIcons.Refresh
                else -> WiIcons.Check
            }
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = colorHero,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun ItemInfra(icono: ImageVector, titulo: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = WiCss.bt,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = titulo,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = WiCss.tx
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = WiCss.tx3,
                lineHeight = 16.sp
            )
        }
    }
}
