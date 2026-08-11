package com.hotelwii.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hotelwii.app.Rutas
import com.hotelwii.core.Wii
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🧩 Drawer.kt — Menú lateral desplegable ultra-delgado para HotelWii.
 */
@Composable
fun Drawer(
    rutaActiva: String,
    onSeleccionarRuta: (String) -> Unit,
    hotelNombre: String = Wii.hotelNombreDefault,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
            .clip(RectangleShape)
            .background(WiCss.wb)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header Drawer: Nombre de Hotel dinámico
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Place,
                        contentDescription = null,
                        tint = WiCss.hv,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = hotelNombre,
                        style = WiText.h3,
                        color = WiCss.tx,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Lista de rutas navegables ordenadas numéricamente
                Rutas.RUTAS_NAV.forEach { meta ->
                    val isSelected = rutaActiva == meta.key
                    val bgColor = if (isSelected) WiCss.hv.copy(alpha = 0.15f) else WiCss.inp.copy(alpha = 0.4f)
                    val textColor = if (isSelected) WiCss.hv else WiCss.tx2

                    val tituloMostrar = meta.nombre ?: meta.titulo

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .clickable { onSeleccionarRuta(meta.key) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = meta.icono,
                            contentDescription = meta.titulo,
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = tituloMostrar,
                            style = WiText.body,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Footer Drawer
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WiCss.inp)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = WiCss.success,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${Wii.app} · ${Wii.hotelUbicacion}",
                            style = WiText.tiny,
                            color = WiCss.success,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
